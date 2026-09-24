package com.springbootedu.testing.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Lesson 3.2 — the body of POST /api/orders.
 */
public record OrderRequest(@NotBlank String isbn, @Positive int quantity) {
}
