package com.springbootedu.corecontainer.scope;

import org.springframework.stereotype.Service;

/**
 * Lesson 3.3 — PITFALL: a prototype injected into a singleton is created only once.
 */
// tag::broken[]
@Service
public class BrokenCheckoutService {

    private final ShoppingCart cart;          // injected once, when this singleton is created

    public BrokenCheckoutService(ShoppingCart cart) {
        this.cart = cart;
    }

    public ShoppingCart startCheckout() {
        return cart;                          // every customer gets the SAME cart!
    }
}
// end::broken[]
