package com.springbootedu.asyncschedulingbatch.exercise2;

/**
 * Given: a line that can never be imported.
 */
public class InvalidOrderLineException extends RuntimeException {

    public InvalidOrderLineException(String message) {
        super(message);
    }
}
