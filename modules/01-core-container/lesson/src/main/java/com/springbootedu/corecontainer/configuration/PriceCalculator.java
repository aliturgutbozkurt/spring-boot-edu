package com.springbootedu.corecontainer.configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Lesson 3.2 — depends on a {@link TaxRate}.
 */
public record PriceCalculator(TaxRate taxRate) {

    public BigDecimal grossPrice(BigDecimal netPrice) {
        return netPrice.add(netPrice.multiply(taxRate.rate())).setScale(2, RoundingMode.HALF_UP);
    }
}
