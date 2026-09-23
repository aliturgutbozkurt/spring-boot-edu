package com.springbootedu.messagingkafka.exercise2;

/**
 * Given: an order with a quantity that can never be paid.
 */
public class InvalidQuantityException extends RuntimeException {

    public InvalidQuantityException(int quantity) {
        super("quantity must be positive, was " + quantity);
    }
}
