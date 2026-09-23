package com.springbootedu.corecontainer.scope;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * Lesson 3.3 — the fix: ask the container for a new prototype instance each time.
 */
// tag::fixed[]
@Service
public class CheckoutService {

    private final ObjectProvider<ShoppingCart> carts;

    public CheckoutService(ObjectProvider<ShoppingCart> carts) {
        this.carts = carts;
    }

    public ShoppingCart startCheckout() {
        return carts.getObject();             // a new cart on every call
    }
}
// end::fixed[]
