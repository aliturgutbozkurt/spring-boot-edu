package com.springbootedu.testing.exercise1;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 * Exercise 1 — write the tests for the untested LoyaltyCalculator (read its Javadoc for the rules).
 * Always create the calculator with Subjects.loyaltyCalculator(): Exercise1MutantsTest uses it to check
 * that your tests catch deliberately broken versions.
 */
class LoyaltyCalculatorTest {

    private final LoyaltyCalculator calculator = Subjects.loyaltyCalculator();

    @Test
    void bronzeCustomersEarnOnePointPerFullTen() {
        // TODO 1a: test the minimum order (19.99 and exactly 20.00) and that only full 10.00 count (29.99)
        fail("TODO 1a " + calculator);
    }

    @Test
    void higherTiersEarnMore() {
        // TODO 1b: the same order for BRONZE, SILVER and GOLD — mind the rounding for SILVER
        fail("TODO 1b");
    }

    @Test
    void theBirthdayMonthDoublesThePoints() {
        // TODO 1c: the birthday bonus
        fail("TODO 1c");
    }

    @Test
    void noOrderEarnsMoreThan500Points() {
        // TODO 1d: the upper limit
        fail("TODO 1d");
    }
}
