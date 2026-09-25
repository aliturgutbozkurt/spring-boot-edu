package com.springbootedu.capstone.contracts.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Published by the order service (topic bookstore.orders, key = order ID) through its outbox.
 */
// tag::order-placed[]
public record OrderPlaced(String orderId, String customerId, List<Line> lines, BigDecimal total, Instant placedAt) {

    public static final String TOPIC = "bookstore.orders";

    public record Line(String isbn, String title, int quantity, BigDecimal unitPrice) {
    }
}
// end::order-placed[]
