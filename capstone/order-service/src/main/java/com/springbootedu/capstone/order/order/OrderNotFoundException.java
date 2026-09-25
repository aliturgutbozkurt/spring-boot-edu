package com.springbootedu.capstone.order.order;

/**
 * Thrown when an order does not exist or belongs to another customer.
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String id) {
        super("order " + id + " not found");
    }
}
