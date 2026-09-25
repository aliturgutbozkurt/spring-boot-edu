package com.springbootedu.capstone.order.order;

/**
 * Exercise 2 — thrown when an order is cancelled a second time.
 */
public class OrderAlreadyCancelledException extends RuntimeException {

    public OrderAlreadyCancelledException(String id) {
        super("order " + id + " is already cancelled");
    }
}
