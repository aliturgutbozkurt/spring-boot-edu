package com.springbootedu.elasticsearch.catalog;

import java.math.BigDecimal;

/**
 * Lesson 3.6 — the data needed to add a book to the catalog.
 */
public record NewBook(String isbn, String title, String author, String description, String category,
                      BigDecimal price) {
}
