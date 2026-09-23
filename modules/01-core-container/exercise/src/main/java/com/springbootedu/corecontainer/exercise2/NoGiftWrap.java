package com.springbootedu.corecontainer.exercise2;

import java.math.BigDecimal;

/**
 * Exercise 2 — given: the feature is switched off.
 */
public class NoGiftWrap implements GiftWrapService {

    @Override
    public BigDecimal price() {
        return BigDecimal.ZERO;
    }

    @Override
    public String describe() {
        return "Hediye paketi yok / No gift wrap";
    }
}
