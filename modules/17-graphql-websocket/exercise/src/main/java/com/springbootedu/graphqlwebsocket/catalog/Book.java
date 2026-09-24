package com.springbootedu.graphqlwebsocket.catalog;

import java.math.BigDecimal;

/**
 * The schema type {@code Book}.
 */
public record Book(String isbn, String title, BigDecimal price, long authorId) {
}
