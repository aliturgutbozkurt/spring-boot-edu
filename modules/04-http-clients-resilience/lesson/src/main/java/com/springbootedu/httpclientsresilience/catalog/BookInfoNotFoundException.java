package com.springbootedu.httpclientsresilience.catalog;

/**
 * Lesson 3.2 — the remote catalog does not know the ISBN (HTTP 404).
 */
public class BookInfoNotFoundException extends RuntimeException {

    public BookInfoNotFoundException(String isbn) {
        super("The catalog has no book with ISBN " + isbn);
    }
}
