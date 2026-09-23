package com.springbootedu.corecontainer.exercise1;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/**
 * Exercise 1 — given: 29.90, free from 500.00.
 */
@Component("standard")
public class StandardShipping implements ShippingCalculator {

    private static final BigDecimal FREE_FROM = new BigDecimal("500.00");

    @Override
    public BigDecimal cost(BigDecimal orderTotal) {
        return orderTotal.compareTo(FREE_FROM) >= 0 ? new BigDecimal("0.00") : new BigDecimal("29.90");
    }
}
