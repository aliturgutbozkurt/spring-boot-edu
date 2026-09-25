package com.springbootedu.kubernetes.book;

import java.math.BigDecimal;

/**
 * Lesson 3.1 — a book of the catalog.
 */
public record Book(String isbn, String title, BigDecimal price) {
}
