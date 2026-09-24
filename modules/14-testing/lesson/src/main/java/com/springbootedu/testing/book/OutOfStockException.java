package com.springbootedu.testing.book;

/**
 * Lesson 3.2 — fewer copies left than ordered.
 */
public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String isbn, int ordered, int available) {
        super("Only " + available + " copies of " + isbn + " left, " + ordered + " ordered");
    }
}
