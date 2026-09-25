package com.springbootedu.capstone.order.stock;

/**
 * Thrown when the catalog did not answer in time or is down (gRPC UNAVAILABLE, DEADLINE_EXCEEDED).
 */
public class CatalogUnavailableException extends RuntimeException {

    public CatalogUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
