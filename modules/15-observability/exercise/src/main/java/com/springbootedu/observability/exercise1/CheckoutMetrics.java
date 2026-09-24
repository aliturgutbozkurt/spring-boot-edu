package com.springbootedu.observability.exercise1;

import io.micrometer.core.instrument.MeterRegistry;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/**
 * Exercise 1 — how many checkouts, by payment method, and how much money.
 */
@Component
public class CheckoutMetrics {

    private final MeterRegistry registry;

    public CheckoutMetrics(MeterRegistry registry) {
        this.registry = registry;
        // TODO 1a: a distribution summary "bookstore.checkout.amount" with the base unit "TRY"
    }

    public void checkout(String paymentMethod, BigDecimal amount) {
        // TODO 1b: count the checkout in "bookstore.checkouts" with the tag payment=<method>
        // TODO 1c: only "card" and "transfer" are real tag values — everything else is counted as "other"
        // TODO 1d: record the amount in the summary
        throw new UnsupportedOperationException("TODO 1 — " + paymentMethod + amount + registry);
    }
}
