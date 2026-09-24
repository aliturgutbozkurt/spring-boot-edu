package com.springbootedu.asyncschedulingbatch.batch;

import java.math.BigDecimal;

/**
 * Lesson 3.3 — a validated book, ready to be written.
 */
public record ImportedBook(String isbn, String title, BigDecimal price) {
}
