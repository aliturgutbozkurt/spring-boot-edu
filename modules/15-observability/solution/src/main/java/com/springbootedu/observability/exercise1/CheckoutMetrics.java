package com.springbootedu.observability.exercise1;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import java.math.BigDecimal;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Exercise 1 — how many checkouts, by payment method, and how much money.
 */
@Component
public class CheckoutMetrics {

    private static final Set<String> KNOWN_METHODS = Set.of("card", "transfer");

    private final MeterRegistry registry;
    private final DistributionSummary amounts;

    public CheckoutMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.amounts = DistributionSummary.builder("bookstore.checkout.amount")
                .baseUnit("TRY")
                .register(registry);
    }

    public void checkout(String paymentMethod, BigDecimal amount) {
        String tag = KNOWN_METHODS.contains(paymentMethod) ? paymentMethod : "other";   // bounded tag values
        Counter.builder("bookstore.checkouts").tag("payment", tag).register(registry).increment();
        amounts.record(amount.doubleValue());
    }
}
