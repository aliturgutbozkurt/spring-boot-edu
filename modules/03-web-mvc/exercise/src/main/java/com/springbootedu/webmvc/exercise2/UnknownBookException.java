package com.springbootedu.webmvc.exercise2;

/**
 * Exercise 2 — given: thrown for an ISBN the shop does not sell.
 */
public class UnknownBookException extends RuntimeException {

    private final String isbn;

    public UnknownBookException(String isbn) {
        super("No book with ISBN " + isbn);
        this.isbn = isbn;
    }

    public String isbn() {
        return isbn;
    }
}
