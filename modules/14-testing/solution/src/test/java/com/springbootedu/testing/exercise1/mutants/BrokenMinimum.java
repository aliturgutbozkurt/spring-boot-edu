package com.springbootedu.testing.exercise1.mutants;

import com.springbootedu.testing.exercise1.CustomerTier;
import com.springbootedu.testing.exercise1.LoyaltyCalculator;
import java.math.BigDecimal;

/**
 * A deliberately broken calculator: an order of exactly 20.00 earns nothing (off by one).
 */
public class BrokenMinimum extends LoyaltyCalculator {

    @Override
    public int points(BigDecimal orderTotal, CustomerTier tier, boolean birthdayMonth) {
        return orderTotal.compareTo(new BigDecimal("20.00")) <= 0 ? 0 : super.points(orderTotal, tier, birthdayMonth);
    }
}
