package com.springbootedu.dockerdeployment.book;

import java.math.BigDecimal;

/**
 * Given — a book of the catalog.
 */
public record Book(String isbn, String title, BigDecimal price) {
}
