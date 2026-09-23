package com.springbootedu.rediscaching.exercise1;

import java.math.BigDecimal;

/**
 * Given: the detail page data of a product.
 */
public record ProductDetail(String id, String name, BigDecimal price) {
}
