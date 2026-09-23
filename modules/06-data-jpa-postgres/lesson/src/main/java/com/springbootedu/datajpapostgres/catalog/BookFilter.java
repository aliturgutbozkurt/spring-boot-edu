package com.springbootedu.datajpapostgres.catalog;

import java.math.BigDecimal;
import org.jspecify.annotations.Nullable;

/**
 * Lesson 3.5 — search criteria; every field is optional.
 */
public record BookFilter(@Nullable String title, @Nullable BigDecimal maxPrice, @Nullable String category) {
}
