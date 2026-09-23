package com.springbootedu.corecontainer.exercise1;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/**
 * Exercise 1 — given: always 59.90.
 */
@Component("express")
public class ExpressShipping implements ShippingCalculator {

    @Override
    public BigDecimal cost(BigDecimal orderTotal) {
        return new BigDecimal("59.90");
    }
}
