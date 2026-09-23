package com.springbootedu.corecontainer.exercise2;

import java.math.BigDecimal;

/**
 * Exercise 2 — given: gift wrapping for a price.
 */
public class PaidGiftWrap implements GiftWrapService {

    private final BigDecimal price;

    public PaidGiftWrap(BigDecimal price) {
        this.price = price;
    }

    @Override
    public BigDecimal price() {
        return price;
    }

    @Override
    public String describe() {
        return "Hediye paketi / Gift wrap: " + price;
    }
}
