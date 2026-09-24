package com.springbootedu.nativeperformance.price;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Lesson 3.4 — bookstore.price-format.class-name: the fully qualified name of a PriceFormat.
 */
@ConfigurationProperties("bookstore.price-format")
public record PriceFormatProperties(String className) {
}
