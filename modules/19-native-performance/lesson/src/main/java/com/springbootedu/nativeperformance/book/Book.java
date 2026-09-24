package com.springbootedu.nativeperformance.book;

import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Lesson 3.2 — a Spring Data JDBC entity (a record).
 */
@Table("book")
public record Book(@Id String isbn, String title, BigDecimal price, int year) {
}
