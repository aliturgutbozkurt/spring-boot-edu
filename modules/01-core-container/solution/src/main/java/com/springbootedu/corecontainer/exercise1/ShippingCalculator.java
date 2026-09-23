package com.springbootedu.corecontainer.exercise1;

import java.math.BigDecimal;

/**
 * Exercise 1 — one implementation per shipping method; the bean name is the method name.
 */
public interface ShippingCalculator {

    BigDecimal cost(BigDecimal orderTotal);
}
