package com.springbootedu.grpc.catalog;

import com.springbootedu.grpc.catalog.v1.Book;
import com.springbootedu.grpc.catalog.v1.BookCatalogGrpc;
import com.springbootedu.grpc.catalog.v1.GetBookRequest;
import com.springbootedu.grpc.catalog.v1.ImportSummary;
import com.springbootedu.grpc.catalog.v1.ListBooksRequest;
import com.springbootedu.grpc.catalog.v1.StockAnswer;
import com.springbootedu.grpc.catalog.v1.StockQuestion;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

/**
 * Lessons 3.2–3.3 — implements the generated base class; every method gets a StreamObserver for the answer.
 */
@GrpcService
class BookCatalogService extends BookCatalogGrpc.BookCatalogImplBase {

    private final BookStore store;

    BookCatalogService(BookStore store) {
        this.store = store;
    }

    // tag::unary[]
    @Override
    public void getBook(GetBookRequest request, StreamObserver<Book> responses) {
        if (!request.getIsbn().matches("\\d{13}")) {
            throw new IllegalArgumentException("An ISBN has 13 digits: " + request.getIsbn());   // → INVALID_ARGUMENT
        }
        Book book = store.find(request.getIsbn()).orElseThrow(() -> new BookNotFoundException(request.getIsbn()));
        responses.onNext(book);                 // exactly one answer …
        responses.onCompleted();                // … and the call is over
    }
    // end::unary[]

    // tag::server-streaming[]
    @Override
    public void listBooks(ListBooksRequest request, StreamObserver<Book> responses) {
        store.byAuthor(request.getAuthor()).forEach(responses::onNext);    // many answers, one after the other
        responses.onCompleted();
    }
    // end::server-streaming[]

    // tag::client-streaming[]
    @Override
    public StreamObserver<Book> importBooks(StreamObserver<ImportSummary> responses) {
        return new StreamObserver<>() {                    // the server returns an observer for the client's stream
            private int imported;
            private int rejected;

            @Override
            public void onNext(Book book) {
                if (book.getIsbn().matches("\\d{13}")) {
                    store.add(book, 1);
                    imported++;
                } else {
                    rejected++;
                }
            }

            @Override
            public void onError(Throwable error) {
                // the client cancelled or the connection broke: nothing to answer
            }

            @Override
            public void onCompleted() {                    // the client has sent everything: one summary
                responses.onNext(ImportSummary.newBuilder().setImported(imported).setRejected(rejected).build());
                responses.onCompleted();
            }
        };
    }
    // end::client-streaming[]

    // tag::bidi-streaming[]
    @Override
    public StreamObserver<StockQuestion> chat(StreamObserver<StockAnswer> responses) {
        return new StreamObserver<>() {
            @Override
            public void onNext(StockQuestion question) {   // answer each question at once, the stream stays open
                responses.onNext(StockAnswer.newBuilder()
                        .setIsbn(question.getIsbn())
                        .setAvailable(store.stockOf(question.getIsbn()))
                        .build());
            }

            @Override
            public void onError(Throwable error) {
            }

            @Override
            public void onCompleted() {
                responses.onCompleted();
            }
        };
    }
    // end::bidi-streaming[]
}
