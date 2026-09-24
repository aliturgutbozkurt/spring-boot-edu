package com.springbootedu.modulith.order;

import java.math.BigDecimal;

/**
 * A placed order.
 */
public record Order(long id, String customerId, String isbn, int quantity, BigDecimal total) {
}
