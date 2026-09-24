package com.springbootedu.testing.exercise1;

import java.util.function.Supplier;

/**
 * Given: creates the calculator for the tests. Exercise1MutantsTest swaps it for broken versions to check
 * that your tests notice the bugs — always use {@code Subjects.loyaltyCalculator()} instead of {@code new}.
 */
public final class Subjects {

    static volatile Supplier<LoyaltyCalculator> calculator = LoyaltyCalculator::new;

    private Subjects() {
    }

    public static LoyaltyCalculator loyaltyCalculator() {
        return calculator.get();
    }
}
