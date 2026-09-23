package com.springbootedu.setupmodernjava.exercise1;

import java.math.BigDecimal;

/**
 * Exercise 1 — discount rate per customer kind.
 */
public final class DiscountPolicy {

    private DiscountPolicy() {
    }

    public static int rateFor(Customer customer) {
        // TODO 1a: use a switch with patterns over the sealed Customer type:
        //          Regular → 0, Student → 10,
        //          Member → 5; 15 from 2 years on; 20 from 5 years on (use "when" guards)
        throw new UnsupportedOperationException("TODO 1a");
    }

    public static BigDecimal priceFor(Customer customer, BigDecimal price) {
        // TODO 1b: apply rateFor(customer) to the price, rounded to 2 decimals (HALF_UP)
        throw new UnsupportedOperationException("TODO 1b");
    }
}
