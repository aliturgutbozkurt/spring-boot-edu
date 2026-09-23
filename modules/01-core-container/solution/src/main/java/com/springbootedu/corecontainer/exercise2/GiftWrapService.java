package com.springbootedu.corecontainer.exercise2;

import java.math.BigDecimal;

/**
 * Exercise 2 — exactly one implementation must be a bean, depending on the feature flag.
 */
public interface GiftWrapService {

    BigDecimal price();

    String describe();
}
