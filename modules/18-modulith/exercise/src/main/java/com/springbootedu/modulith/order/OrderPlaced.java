package com.springbootedu.modulith.order;

import java.math.BigDecimal;

/**
 * The event of the order module (given).
 */
public record OrderPlaced(long orderId, String customerId, String isbn, int quantity, BigDecimal total) {
}
