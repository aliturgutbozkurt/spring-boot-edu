package com.springbootedu.testing.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

/**
 * Lesson 3.5 — the answer to an order. Money is written as a string ("179.80") so no client rounds it.
 */
// tag::confirmation[]
public record OrderConfirmation(String isbn, String title, int quantity,
                                @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal total,
                                String paymentId) {
}
// end::confirmation[]
