package com.springbootedu.reactive.review;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Lesson 3.5 — a review, stored in MongoDB.
 */
@Document("reviews")
public record Review(@Id @Nullable String id, String isbn, String author, int stars, String text) {
}
