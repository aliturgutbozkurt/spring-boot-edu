package com.springbootedu.capstone.order.stock;

/**
 * Thrown when the catalog has not enough copies (gRPC FAILED_PRECONDITION).
 */
public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
