package com.springbootedu.grpc.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.grpc.catalog.v1.Book;
import com.springbootedu.grpc.catalog.v1.BookCatalogGrpc;
import com.springbootedu.grpc.catalog.v1.GetBookRequest;
import com.springbootedu.grpc.catalog.v1.ImportSummary;
import com.springbootedu.grpc.catalog.v1.ListBooksRequest;
import com.springbootedu.grpc.catalog.v1.StockAnswer;
import com.springbootedu.grpc.catalog.v1.StockQuestion;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Lessons 3.2–3.4 — the four kinds of RPC and the error mapping, over an in-process transport (no port).
 */
// tag::test-transport[]
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@AutoConfigureTestGrpcTransport                       // in-process channels: fast, no network port
class BookCatalogServiceTest {

    @Autowired
    BookCatalogGrpc.BookCatalogBlockingStub catalog;  // the application's stub (ClientConfiguration), now in-process

    @Autowired
    BookCatalogGrpc.BookCatalogStub asyncCatalog;     // for client and bidirectional streaming

    @Test
    void unaryCall() {
        Book book = catalog.getBook(GetBookRequest.newBuilder().setIsbn("9780134685991").build());

        assertThat(book.getTitle()).isEqualTo("Effective Java");
        assertThat(book.getPriceCents()).isEqualTo(8990);
    }
    // end::test-transport[]

    @Test
    void anUnknownBookIsNotFound() {
        assertThatThrownBy(() -> catalog.getBook(GetBookRequest.newBuilder().setIsbn("0000000000000").build()))
                .isInstanceOfSatisfying(StatusRuntimeException.class, e -> {
                    assertThat(e.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND);
                    assertThat(e.getStatus().getDescription()).contains("0000000000000");
                });
    }

    @Test
    void aMalformedIsbnIsAnInvalidArgument() {
        assertThatThrownBy(() -> catalog.getBook(GetBookRequest.newBuilder().setIsbn("abc").build()))
                .isInstanceOfSatisfying(StatusRuntimeException.class,
                        e -> assertThat(e.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT));
    }

    @Test
    void serverStreaming() {
        List<String> titles = new java.util.ArrayList<>();
        catalog.listBooks(ListBooksRequest.newBuilder().setAuthor("Joshua Bloch").build())
                .forEachRemaining(book -> titles.add(book.getTitle()));

        assertThat(titles).containsExactly("Effective Java", "Java Puzzlers");
    }

    // tag::client-streaming[]
    @Test
    void clientStreaming() throws Exception {
        var summary = new CompletableFuture<ImportSummary>();
        StreamObserver<Book> upload = asyncCatalog.importBooks(new StreamObserver<>() {
            @Override
            public void onNext(ImportSummary value) {
                summary.complete(value);
            }

            @Override
            public void onError(Throwable error) {
                summary.completeExceptionally(error);
            }

            @Override
            public void onCompleted() {
            }
        });

        upload.onNext(book("9781098150358", "Learning Spring Boot 3.0"));
        upload.onNext(book("123", "Broken ISBN"));
        upload.onNext(book("9781617294945", "Spring Microservices in Action"));
        upload.onCompleted();                           // the client says: no more books

        ImportSummary result = summary.get(5, TimeUnit.SECONDS);
        assertThat(result.getImported()).isEqualTo(2);
        assertThat(result.getRejected()).isEqualTo(1);
    }
    // end::client-streaming[]

    @Test
    void bidirectionalStreaming() throws Exception {
        List<StockAnswer> answers = new CopyOnWriteArrayList<>();
        var done = new CompletableFuture<Void>();
        StreamObserver<StockQuestion> questions = asyncCatalog.chat(new StreamObserver<>() {
            @Override
            public void onNext(StockAnswer answer) {
                answers.add(answer);
            }

            @Override
            public void onError(Throwable error) {
                done.completeExceptionally(error);
            }

            @Override
            public void onCompleted() {
                done.complete(null);
            }
        });

        questions.onNext(StockQuestion.newBuilder().setIsbn("9780134685991").build());
        questions.onNext(StockQuestion.newBuilder().setIsbn("9781617297571").build());
        questions.onCompleted();

        done.get(5, TimeUnit.SECONDS);
        assertThat(answers).extracting(StockAnswer::getIsbn).containsExactly("9780134685991", "9781617297571");
        assertThat(answers).extracting(StockAnswer::getAvailable).containsExactly(12, 3);
    }

    private static Book book(String isbn, String title) {
        return Book.newBuilder().setIsbn(isbn).setTitle(title).setAuthor("Various").setPriceCents(4500).build();
    }
}
