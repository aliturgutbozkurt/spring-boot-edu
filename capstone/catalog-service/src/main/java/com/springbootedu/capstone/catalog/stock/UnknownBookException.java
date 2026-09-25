package com.springbootedu.capstone.catalog.stock;

/**
 * An order line for an ISBN that is not in the catalog (gRPC status NOT_FOUND).
 */
public class UnknownBookException extends RuntimeException {

    public UnknownBookException(String isbn) {
        super("No book with ISBN " + isbn);
    }
}
