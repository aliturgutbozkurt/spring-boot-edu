package com.springbootedu.webmvc.exercise2;

/**
 * Exercise 2 — given: thrown when fewer copies are in stock than ordered.
 */
public class OutOfStockException extends RuntimeException {

    private final String isbn;
    private final int requested;
    private final int available;

    public OutOfStockException(String isbn, int requested, int available) {
        super("Only " + available + " of " + requested + " requested copies are in stock");
        this.isbn = isbn;
        this.requested = requested;
        this.available = available;
    }

    public String isbn() {
        return isbn;
    }

    public int requested() {
        return requested;
    }

    public int available() {
        return available;
    }
}
