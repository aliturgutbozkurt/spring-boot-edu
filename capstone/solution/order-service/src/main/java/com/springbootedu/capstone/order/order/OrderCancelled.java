package com.springbootedu.capstone.order.order;

import com.springbootedu.capstone.contracts.events.OrderPlaced;
import java.time.Instant;
import java.util.List;

/**
 * Exercise 2 — published through the outbox when an order is cancelled (key = order ID).
 * <p>
 * Its own topic: the consumers of bookstore.orders read every message as OrderPlaced (no type header), so a
 * cancellation there would be counted as a sale. Once another service consumes it, it moves to the contracts.
 */
public record OrderCancelled(String orderId, String customerId, List<OrderPlaced.Line> lines, Instant cancelledAt) {

    public static final String TOPIC = "bookstore.order-cancellations";
}
