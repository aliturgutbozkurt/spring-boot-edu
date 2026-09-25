package com.springbootedu.springai.exercise3;

import org.springframework.stereotype.Component;

/**
 * Exercise 3 — a tool for the status of an order.
 */
@Component
public class OrderTools {

    private final OrderBook orders;

    OrderTools(OrderBook orders) {
        this.orders = orders;
    }

    // TODO 3a: make this method a tool with a description for the model (what it returns, when to use it),
    //          and describe the parameter (the order ID, for example A-1001)
    public String orderStatus(String orderId) {
        return orders.statusOf(orderId).orElse("UNKNOWN ORDER");
    }
}
