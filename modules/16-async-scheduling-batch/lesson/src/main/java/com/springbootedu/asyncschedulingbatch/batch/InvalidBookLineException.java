package com.springbootedu.asyncschedulingbatch.batch;

/**
 * Lesson 3.4 — a line that can never be imported: it is skipped, not retried.
 */
public class InvalidBookLineException extends RuntimeException {

    public InvalidBookLineException(String message) {
        super(message);
    }
}
