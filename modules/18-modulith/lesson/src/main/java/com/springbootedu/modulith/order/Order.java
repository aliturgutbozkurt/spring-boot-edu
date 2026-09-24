package com.springbootedu.modulith.order;

import java.math.BigDecimal;

/**
 * Lesson 3.2 — a placed order.
 */
public record Order(long id, String customerId, String isbn, int quantity, BigDecimal total) {
}
