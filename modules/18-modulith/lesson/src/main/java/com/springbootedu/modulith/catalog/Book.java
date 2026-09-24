package com.springbootedu.modulith.catalog;

import java.math.BigDecimal;

/**
 * Lesson 3.1 — part of the catalog module's API.
 */
public record Book(String isbn, String title, BigDecimal price) {
}
