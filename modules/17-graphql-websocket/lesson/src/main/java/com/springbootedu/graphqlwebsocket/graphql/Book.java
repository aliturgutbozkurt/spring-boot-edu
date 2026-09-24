package com.springbootedu.graphqlwebsocket.graphql;

import java.math.BigDecimal;

/**
 * Lesson 3.1 — a row of the book table; the schema field {@code author} is resolved separately from {@code authorId}.
 */
public record Book(String isbn, String title, BigDecimal price, long authorId) {
}
