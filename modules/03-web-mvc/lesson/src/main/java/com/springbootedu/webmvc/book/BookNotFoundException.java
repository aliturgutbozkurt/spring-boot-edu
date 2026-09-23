package com.springbootedu.webmvc.book;

/**
 * Lesson 3.3 — a domain exception; the web layer turns it into a 404 ProblemDetail.
 */
public class BookNotFoundException extends RuntimeException {

    private final long bookId;

    public BookNotFoundException(long bookId) {
        super("No book with id " + bookId);
        this.bookId = bookId;
    }

    public long bookId() {
        return bookId;
    }
}
