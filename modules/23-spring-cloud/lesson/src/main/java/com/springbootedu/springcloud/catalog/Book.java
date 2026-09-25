package com.springbootedu.springcloud.catalog;

import java.math.BigDecimal;

/**
 * Lesson 3.2 — the answer of the catalog service.
 */
public record Book(String isbn, String title, BigDecimal price, String servedBy) {
}
