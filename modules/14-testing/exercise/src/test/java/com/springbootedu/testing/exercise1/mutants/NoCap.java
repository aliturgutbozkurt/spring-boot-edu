package com.springbootedu.testing.exercise1.mutants;

import com.springbootedu.testing.exercise1.CustomerTier;
import com.springbootedu.testing.exercise1.LoyaltyCalculator;
import java.math.BigDecimal;

/**
 * A deliberately broken calculator: there is no upper limit.
 */
public class NoCap extends LoyaltyCalculator {

    @Override
    public int points(BigDecimal orderTotal, CustomerTier tier, boolean birthdayMonth) {
        int base = orderTotal.divideToIntegralValue(BigDecimal.TEN).intValue();
        int points = switch (tier) {
            case BRONZE -> base;
            case SILVER -> base * 3 / 2;
            case GOLD -> base * 2;
        };
        return orderTotal.compareTo(new BigDecimal("20.00")) < 0 ? 0 : (birthdayMonth ? points * 2 : points);
    }
}
