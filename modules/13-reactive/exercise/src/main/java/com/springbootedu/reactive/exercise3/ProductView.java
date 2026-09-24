package com.springbootedu.reactive.exercise3;

import java.math.BigDecimal;

/**
 * Given: everything the product page shows.
 */
public record ProductView(String isbn, BigDecimal price, int inStock, double rating) {
}
