package com.springbootedu.capstone.order.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * An order as the API returns it.
 */
public record OrderResponse(String id, String customerId, List<OrderLine> lines, BigDecimal total, Instant placedAt,
                            Order.Status status) {

    static OrderResponse from(Order order) {
        return new OrderResponse(order.getId().toString(), order.getCustomerId(), order.getLines(), order.getTotal(),
                order.getPlacedAt(), order.getStatus());
    }
}
