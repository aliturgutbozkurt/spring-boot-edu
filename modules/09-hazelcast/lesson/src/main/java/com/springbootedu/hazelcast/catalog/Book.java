package com.springbootedu.hazelcast.catalog;

import java.math.BigDecimal;

/**
 * Lesson 3.2 — a plain record. Hazelcast serializes records with Compact serialization, no configuration needed.
 */
public record Book(String isbn, String title, BigDecimal price) {
}
