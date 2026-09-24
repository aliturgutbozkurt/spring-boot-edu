package com.springbootedu.graphqlwebsocket.catalog;

/**
 * Thrown when an order wants more copies than the inventory has.
 */
public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String isbn, int wanted, int available) {
        super("Not enough stock for " + isbn + ": wanted " + wanted + ", available " + available);
    }
}
