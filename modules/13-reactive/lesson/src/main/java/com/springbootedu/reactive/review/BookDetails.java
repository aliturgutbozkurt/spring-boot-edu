package com.springbootedu.reactive.review;

import java.math.BigDecimal;
import java.util.List;

/**
 * Lesson 3.5 — the book (PostgreSQL) together with its reviews (MongoDB).
 */
public record BookDetails(String isbn, String title, BigDecimal price, double averageStars, List<Review> reviews) {
}
