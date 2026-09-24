package com.springbootedu.testing.book;

/**
 * Lesson 3.2 — no book with this ISBN.
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String isbn) {
        super("No book with ISBN " + isbn);
    }
}
