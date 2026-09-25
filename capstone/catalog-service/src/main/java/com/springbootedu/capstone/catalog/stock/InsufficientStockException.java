package com.springbootedu.capstone.catalog.stock;

/**
 * Not enough copies for an order line (gRPC status FAILED_PRECONDITION).
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String isbn, int wanted, int available) {
        super("Not enough stock for " + isbn + ": wanted " + wanted + ", available " + available);
    }
}
