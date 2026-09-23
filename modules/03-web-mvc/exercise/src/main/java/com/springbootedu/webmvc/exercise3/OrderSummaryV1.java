package com.springbootedu.webmvc.exercise3;

import java.math.BigDecimal;

/**
 * Exercise 3 — given: version 1 of the response.
 */
public record OrderSummaryV1(long id, BigDecimal total, String status) {

    static OrderSummaryV1 from(OrderSummary order) {
        return new OrderSummaryV1(order.id(), order.total(), order.status().name());
    }
}
