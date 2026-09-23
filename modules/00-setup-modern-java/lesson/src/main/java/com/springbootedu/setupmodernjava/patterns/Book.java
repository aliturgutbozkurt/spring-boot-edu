package com.springbootedu.setupmodernjava.patterns;

import com.springbootedu.setupmodernjava.records.Money;

/**
 * Lesson 3.3 — a record nested inside {@link OrderLine}.
 */
public record Book(String title, Money price, Format format) {
}
