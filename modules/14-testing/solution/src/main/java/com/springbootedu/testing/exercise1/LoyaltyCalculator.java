package com.springbootedu.testing.exercise1;

import java.math.BigDecimal;

/**
 * Exercise 1 — given, correct, but completely untested. The rules:
 * <ul>
 *   <li>orders below 20.00 earn no points</li>
 *   <li>1 point per full 10.00 of the order total</li>
 *   <li>SILVER earns ×1.5 (rounded down), GOLD ×2</li>
 *   <li>in the customer's birthday month the points are doubled</li>
 *   <li>at most 500 points per order</li>
 * </ul>
 */
public class LoyaltyCalculator {

    static final BigDecimal MINIMUM_ORDER = new BigDecimal("20.00");
    static final int MAXIMUM_POINTS = 500;

    public int points(BigDecimal orderTotal, CustomerTier tier, boolean birthdayMonth) {
        if (orderTotal.compareTo(MINIMUM_ORDER) < 0) {
            return 0;
        }
        int base = orderTotal.divideToIntegralValue(BigDecimal.TEN).intValue();
        int points = switch (tier) {
            case BRONZE -> base;
            case SILVER -> base * 3 / 2;
            case GOLD -> base * 2;
        };
        if (birthdayMonth) {
            points *= 2;
        }
        return Math.min(points, MAXIMUM_POINTS);
    }
}
