package com.springbootedu.corecontainer.exercise1;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/**
 * Exercise 1 — picking the order up in the store is free.
 */
@Component("pickup")
public class StorePickupShipping implements ShippingCalculator {

    @Override
    public BigDecimal cost(BigDecimal orderTotal) {
        return new BigDecimal("0.00");
    }
}
