package com.springbootedu.springai.exercise3;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
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

    @Tool(description = "Returns the status of an order. Use it when a customer asks where an order is.")
    public String orderStatus(@ToolParam(description = "the order ID, for example A-1001") String orderId) {
        return orders.statusOf(orderId).orElse("UNKNOWN ORDER");
    }
}
