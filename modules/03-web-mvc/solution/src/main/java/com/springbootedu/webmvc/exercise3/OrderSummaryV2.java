package com.springbootedu.webmvc.exercise3;

import java.math.BigDecimal;
import java.util.List;

/**
 * Exercise 3 — version 2 of the response: money object, status with label, order lines.
 */
public record OrderSummaryV2(long id, Money total, StatusInfo status, List<OrderSummary.Line> lines) {

    public record Money(BigDecimal amount, String currency) {
    }

    public record StatusInfo(String code, String label) {
    }

    static OrderSummaryV2 from(OrderSummary order) {
        return new OrderSummaryV2(order.id(), new Money(order.total(), "TRY"),
                new StatusInfo(order.status().name(), order.status().label()), order.lines());
    }
}
