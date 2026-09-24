package com.springbootedu.graphqlwebsocket.graphql;

/**
 * Lesson 3.1 — the schema type {@code Review}.
 */
public record Review(long id, String isbn, int stars, String text) {
}
