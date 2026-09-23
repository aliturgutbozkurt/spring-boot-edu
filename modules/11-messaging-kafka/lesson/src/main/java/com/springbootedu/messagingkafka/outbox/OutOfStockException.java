package com.springbootedu.messagingkafka.outbox;

/**
 * Lesson 3.6 — not enough copies left; the whole order is rolled back.
 */
public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String isbn, int quantity) {
        super("Not enough copies of " + isbn + " for " + quantity);
    }
}
