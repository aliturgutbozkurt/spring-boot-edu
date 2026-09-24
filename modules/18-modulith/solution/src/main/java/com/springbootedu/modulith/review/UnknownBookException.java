package com.springbootedu.modulith.review;

/**
 * A review for a book that is not in the catalog.
 */
public class UnknownBookException extends RuntimeException {

    public UnknownBookException(String isbn) {
        super("No book with ISBN " + isbn);
    }
}
