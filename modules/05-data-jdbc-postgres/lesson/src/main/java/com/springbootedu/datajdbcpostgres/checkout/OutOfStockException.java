package com.springbootedu.datajdbcpostgres.checkout;

/**
 * Lesson 3.5 — a RuntimeException, so @Transactional rolls back by default.
 */
public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String isbn, int requested, int available) {
        super("Only " + available + " of " + requested + " copies of " + isbn + " in stock");
    }
}
