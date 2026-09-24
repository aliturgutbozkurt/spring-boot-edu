package com.springbootedu.observability.exercise3;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Exercise 3 — "paymentProvider" in /actuator/health.
 */
@Component
public class PaymentProviderHealthIndicator implements HealthIndicator {

    private final PaymentProvider provider;

    public PaymentProviderHealthIndicator(PaymentProvider provider) {
        this.provider = provider;
    }

    @Override
    public Health health() {
        // TODO 3a: ping the provider; maintenance → OUT_OF_SERVICE
        // TODO 3b: slower than 500 ms → DOWN with the detail reason = "slower than 500 ms"
        // TODO 3c: otherwise UP with the detail responseTimeMs
        // TODO 3d: an exception → DOWN with the error
        return Health.unknown().withDetail("todo", provider.getClass().getSimpleName()).build();
    }
}
