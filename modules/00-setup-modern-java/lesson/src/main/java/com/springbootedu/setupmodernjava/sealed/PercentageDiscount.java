package com.springbootedu.setupmodernjava.sealed;

/**
 * Lesson 3.2 — records are implicitly final, so they can implement a sealed interface directly.
 */
public record PercentageDiscount(int percent) implements Discount {

    public PercentageDiscount {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("percent must be 0..100: " + percent);
        }
    }
}
