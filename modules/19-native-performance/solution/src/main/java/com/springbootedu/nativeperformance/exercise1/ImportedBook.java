package com.springbootedu.nativeperformance.exercise1;

import java.math.BigDecimal;

/**
 * Exercise 1 — read from JSON with Jackson (reflection on the record's constructor and accessors).
 */
public record ImportedBook(String isbn, String title, BigDecimal price) {
}
