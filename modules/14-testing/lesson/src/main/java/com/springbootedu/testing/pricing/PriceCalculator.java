package com.springbootedu.testing.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Lesson 3.1 — plain Java: no Spring, no database. A unit test needs nothing but {@code new}.
 */
// tag::calculator[]
public class PriceCalculator {

    public BigDecimal total(BigDecimal unitPrice, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive, was " + quantity);
        }
        BigDecimal gross = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal discount = quantity >= 10 ? new BigDecimal("0.10")          // 10 % from 10 copies
                : quantity >= 5 ? new BigDecimal("0.05")                        //  5 % from 5 copies
                : BigDecimal.ZERO;
        return gross.subtract(gross.multiply(discount)).setScale(2, RoundingMode.HALF_UP);
    }
}
// end::calculator[]
