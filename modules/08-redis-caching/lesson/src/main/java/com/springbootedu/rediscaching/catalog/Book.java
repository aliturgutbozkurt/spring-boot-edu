package com.springbootedu.rediscaching.catalog;

import java.math.BigDecimal;

/**
 * A cached value: stored as JSON in Redis.
 */
public record Book(String isbn, String title, BigDecimal price) {
}
