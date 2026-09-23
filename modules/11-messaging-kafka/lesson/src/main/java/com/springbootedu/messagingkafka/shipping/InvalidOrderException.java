package com.springbootedu.messagingkafka.shipping;

/**
 * Lesson 3.3 — an order that will never become valid: retrying it is pointless.
 */
public class InvalidOrderException extends RuntimeException {

    public InvalidOrderException(String message) {
        super(message);
    }
}
