package com.springbootedu.corecontainer.configuration;

import java.math.BigDecimal;

/**
 * Lesson 3.2 — a plain value object, registered with {@code @Bean}.
 */
public record TaxRate(BigDecimal rate) {
}
