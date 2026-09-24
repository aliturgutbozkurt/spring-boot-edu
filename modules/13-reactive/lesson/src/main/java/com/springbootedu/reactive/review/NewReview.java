package com.springbootedu.reactive.review;

/**
 * Lesson 3.5 — the body of POST /api/books/{isbn}/reviews.
 */
public record NewReview(String author, int stars, String text) {
}
