package com.springbootedu.springcloud.order;

import java.math.BigDecimal;
import org.jspecify.annotations.Nullable;

/**
 * Lesson 3.4 — PRICED: the catalog answered; PENDING: the fallback, the price comes later.
 */
public record OrderResponse(String isbn, int quantity, @Nullable BigDecimal total, Status status,
                            String pricedBy, String client) {

    public enum Status { PRICED, PENDING }
}
