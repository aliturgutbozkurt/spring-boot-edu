package com.springbootedu.modulith.order;

/**
 * Lesson 3.2 — an order for a book that is not in the catalog.
 */
public class UnknownBookException extends RuntimeException {

    public UnknownBookException(String isbn) {
        super("No book with ISBN " + isbn);
    }
}
