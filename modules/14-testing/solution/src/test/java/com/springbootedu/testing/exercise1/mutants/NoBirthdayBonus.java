package com.springbootedu.testing.exercise1.mutants;

import com.springbootedu.testing.exercise1.CustomerTier;
import com.springbootedu.testing.exercise1.LoyaltyCalculator;
import java.math.BigDecimal;

/**
 * A deliberately broken calculator: the birthday bonus is forgotten.
 */
public class NoBirthdayBonus extends LoyaltyCalculator {

    @Override
    public int points(BigDecimal orderTotal, CustomerTier tier, boolean birthdayMonth) {
        return super.points(orderTotal, tier, false);
    }
}
