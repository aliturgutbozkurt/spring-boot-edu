package com.springbootedu.reactive.book;

import java.math.BigDecimal;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Lesson 3.4 — a row of the table "book". The id is null until PostgreSQL generates it.
 */
@Table("book")
public record Book(@Id @Nullable Long id, String isbn, String title, BigDecimal price) {
}
