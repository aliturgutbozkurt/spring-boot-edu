package com.springbootedu.elasticsearch.catalog;

import java.math.BigDecimal;

/**
 * Lesson 3.6 — a book as stored in PostgreSQL.
 */
public record Book(long id, String isbn, String title, String author, String description, String category,
                   BigDecimal price) {
}
