package com.springbootedu.modulith.catalog;

import java.math.BigDecimal;

/**
 * Part of the catalog module's API.
 */
public record Book(String isbn, String title, BigDecimal price) {
}
