package com.springbootedu.springcloud.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Lesson 3.4 — the body of POST /api/orders.
 */
public record OrderRequest(@NotBlank String isbn, @Min(1) int quantity) {
}
