package com.springbootedu.grpc.catalog;

/**
 * Lesson 3.4 — mapped to the gRPC status NOT_FOUND by {@link CatalogExceptionAdvice}.
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String isbn) {
        super("No book with ISBN " + isbn);
    }
}
