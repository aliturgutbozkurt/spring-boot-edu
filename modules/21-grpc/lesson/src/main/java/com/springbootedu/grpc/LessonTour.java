package com.springbootedu.grpc;

import com.springbootedu.grpc.catalog.v1.BookCatalogGrpc;
import com.springbootedu.grpc.catalog.v1.GetBookRequest;
import com.springbootedu.grpc.catalog.v1.ListBooksRequest;
import com.springbootedu.grpc.client.CatalogClient;
import io.grpc.StatusRuntimeException;
import java.time.Duration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

/**
 * Runs the lesson's examples once (section 3): the application calls its own gRPC server on port 9090.
 * Start it with: ./mvnw -pl modules/21-grpc/lesson spring-boot:run
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final BookCatalogGrpc.BookCatalogBlockingStub catalog;
    private final CatalogClient client;

    LessonTour(BookCatalogGrpc.BookCatalogBlockingStub catalog, CatalogClient client) {
        this.catalog = catalog;
        this.client = client;
    }

    @Override
    public void run(ApplicationArguments args) {
        section("3.2 Unary: GetBook");
        var book = catalog.getBook(GetBookRequest.newBuilder().setIsbn("9780134685991").build());
        print(book.getTitle() + " by " + book.getAuthor() + ", " + book.getPriceCents() + " cents, "
              + book.getSerializedSize() + " bytes on the wire");

        section("3.3 Server streaming: ListBooks");
        catalog.listBooks(ListBooksRequest.getDefaultInstance()).forEachRemaining(each -> print(each.getTitle()));

        section("3.4 Errors are status codes");
        try {
            catalog.getBook(GetBookRequest.newBuilder().setIsbn("0000000000000").build());
        } catch (StatusRuntimeException e) {
            print(e.getStatus().getCode() + ": " + e.getStatus().getDescription());
        }

        section("3.6 A client with a deadline");
        print(client.titleOf("9781617297571", Duration.ofSeconds(1)));
        print("try it with grpcurl: grpcurl -plaintext localhost:9090 list   (Ctrl+C stops the app)");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    private static void print(Object line) {
        System.out.println("  " + line);
    }
}
