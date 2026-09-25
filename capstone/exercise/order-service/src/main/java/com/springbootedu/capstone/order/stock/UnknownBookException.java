package com.springbootedu.capstone.order.stock;

/**
 * Thrown when the catalog does not know an ISBN (gRPC NOT_FOUND).
 */
public class UnknownBookException extends RuntimeException {

    public UnknownBookException(String message, Throwable cause) {
        super(message, cause);
    }
}
