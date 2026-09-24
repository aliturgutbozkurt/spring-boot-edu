package com.springbootedu.modulith.order;

/**
 * An order for a book that is not in the catalog.
 */
public class UnknownBookException extends RuntimeException {

    public UnknownBookException(String isbn) {
        super("No book with ISBN " + isbn);
    }
}
