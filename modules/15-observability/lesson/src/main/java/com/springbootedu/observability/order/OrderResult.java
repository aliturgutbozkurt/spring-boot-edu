package com.springbootedu.observability.order;

import java.math.BigDecimal;

/**
 * Lesson 3.5 — the answer to an order, including the trace id to find the request in Grafana.
 */
public record OrderResult(String isbn, String title, BigDecimal price, String traceId) {
}
