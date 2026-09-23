package com.springbootedu.rediscaching.catalog;

/**
 * Thrown for unknown ISBNs. Exceptions are never cached.
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String isbn) {
        super("No book with ISBN " + isbn);
    }
}
