package com.springbootedu.grpc.client;

import com.springbootedu.grpc.catalog.v1.BookCatalogGrpc;
import com.springbootedu.grpc.catalog.v1.GetBookRequest;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.6 — a client that uses the generated stub like any other bean.
 */
// tag::client[]
@Component
public class CatalogClient {

    private final BookCatalogGrpc.BookCatalogBlockingStub catalog;

    CatalogClient(BookCatalogGrpc.BookCatalogBlockingStub catalog) {      // imported by @ImportGrpcClients
        this.catalog = catalog;
    }

    public String titleOf(String isbn, Duration deadline) {
        return catalog.withDeadlineAfter(deadline.toMillis(), TimeUnit.MILLISECONDS)   // give up after this time
                .getBook(GetBookRequest.newBuilder().setIsbn(isbn).build())
                .getTitle();
    }
}
// end::client[]
