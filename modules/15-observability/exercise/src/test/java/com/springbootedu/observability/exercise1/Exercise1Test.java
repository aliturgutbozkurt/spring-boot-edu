package com.springbootedu.observability.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * Exercise 1 — business metrics for the checkout.
 */
class Exercise1Test {

    private final SimpleMeterRegistry registry = new SimpleMeterRegistry();          // in-memory, no Spring
    private final CheckoutMetrics metrics = new CheckoutMetrics(registry);

    @Test
    void checkoutsAreCountedPerPaymentMethod() {
        metrics.checkout("card", new BigDecimal("89.90"));
        metrics.checkout("card", new BigDecimal("55.00"));
        metrics.checkout("transfer", new BigDecimal("120.00"));

        assertThat(registry.get("bookstore.checkouts").tag("payment", "card").counter().count()).isEqualTo(2);
        assertThat(registry.get("bookstore.checkouts").tag("payment", "transfer").counter().count()).isEqualTo(1);
    }

    @Test
    void theAmountsAreSummarised() {
        metrics.checkout("card", new BigDecimal("89.90"));
        metrics.checkout("card", new BigDecimal("10.10"));

        var amounts = registry.get("bookstore.checkout.amount").summary();
        assertThat(amounts.count()).isEqualTo(2);
        assertThat(amounts.totalAmount()).isEqualTo(100.0);
        assertThat(amounts.max()).isEqualTo(89.90);
        assertThat(amounts.getId().getBaseUnit()).isEqualTo("TRY");
    }

    @Test
    void unknownPaymentMethodsShareOneTagValue() {
        metrics.checkout("coupon-7f3a", BigDecimal.ONE);                            // free text from a client
        metrics.checkout("gift-card-XYZ", BigDecimal.ONE);

        assertThat(registry.get("bookstore.checkouts").tag("payment", "other").counter().count()).isEqualTo(2);
        assertThat(registry.find("bookstore.checkouts").counters()).hasSize(1);     // no new time series per value
    }
}
