package com.springbootedu.setupmodernjava.exercise1;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Exercise 1 — discount rate per customer kind.
 */
public final class DiscountPolicy {

    private DiscountPolicy() {
    }

    public static int rateFor(Customer customer) {
        return switch (customer) {
            case Regular _ -> 0;
            case Student _ -> 10;
            case Member(int years) when years >= 5 -> 20;
            case Member(int years) when years >= 2 -> 15;
            case Member _ -> 5;
        };
    }

    public static BigDecimal priceFor(Customer customer, BigDecimal price) {
        var factor = BigDecimal.valueOf(100 - rateFor(customer)).movePointLeft(2);
        return price.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
