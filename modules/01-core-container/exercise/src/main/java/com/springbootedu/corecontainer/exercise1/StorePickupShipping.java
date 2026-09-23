package com.springbootedu.corecontainer.exercise1;

import java.math.BigDecimal;

/**
 * Exercise 1 — picking the order up in the store is free.
 */
// TODO 1a: register this class as a bean named "pickup" (look at StandardShipping)
public class StorePickupShipping implements ShippingCalculator {

    @Override
    public BigDecimal cost(BigDecimal orderTotal) {
        // TODO 1a: store pickup costs nothing — return 0.00
        throw new UnsupportedOperationException("TODO 1a");
    }
}
