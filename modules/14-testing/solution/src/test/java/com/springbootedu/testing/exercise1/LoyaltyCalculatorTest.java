package com.springbootedu.testing.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

/**
 * Exercise 1 — tests for the untested LoyaltyCalculator.
 */
class LoyaltyCalculatorTest {

    private final LoyaltyCalculator calculator = Subjects.loyaltyCalculator();

    @ParameterizedTest(name = "{0} → {1} points")
    @CsvSource({
            "19.99, 0",               // just below the minimum
            "20.00, 2",               // exactly the minimum
            "29.99, 2",               // only full 10.00 count
            "35.00, 3"})
    void bronzeCustomersEarnOnePointPerFullTen(String total, int expected) {
        assertThat(calculator.points(new BigDecimal(total), CustomerTier.BRONZE, false)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{0} → {1} points")
    @CsvSource({"BRONZE, 5", "SILVER, 7", "GOLD, 10"})    // 50.00 → 5 base points; 5 × 1.5 = 7.5 → 7
    void higherTiersEarnMore(CustomerTier tier, int expected) {
        assertThat(calculator.points(new BigDecimal("50.00"), tier, false)).isEqualTo(expected);
    }

    @Test
    void theBirthdayMonthDoublesThePoints() {
        assertThat(calculator.points(new BigDecimal("50.00"), CustomerTier.GOLD, true)).isEqualTo(20);
    }

    @Test
    void noOrderEarnsMoreThan500Points() {
        assertThat(calculator.points(new BigDecimal("9999.00"), CustomerTier.GOLD, true)).isEqualTo(500);
    }
}
