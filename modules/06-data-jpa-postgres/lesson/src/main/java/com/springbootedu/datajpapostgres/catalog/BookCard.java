package com.springbootedu.datajpapostgres.catalog;

import java.math.BigDecimal;

/**
 * Lessons 3.4–3.5 — a read model for lists: not an entity, never managed, cheap to create.
 */
public record BookCard(String title, String author, BigDecimal price) {
}
