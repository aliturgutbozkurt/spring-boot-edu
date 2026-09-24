package com.springbootedu.graphqlwebsocket.exercise2;

/**
 * An order that makes no sense (no lines, a quantity below 1).
 */
public class InvalidOrderException extends RuntimeException {

    public InvalidOrderException(String message) {
        super(message);
    }
}
