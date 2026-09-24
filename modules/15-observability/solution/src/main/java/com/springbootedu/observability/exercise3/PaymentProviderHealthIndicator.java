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
        try {
            ProviderStatus status = provider.ping();
            if (status.maintenance()) {
                return Health.outOfService().build();
            }
            long millis = status.responseTime().toMillis();
            if (millis > 500) {
                return Health.down().withDetail("reason", "slower than 500 ms").withDetail("responseTimeMs", millis)
                        .build();
            }
            return Health.up().withDetail("responseTimeMs", millis).build();
        } catch (RuntimeException e) {
            return Health.down(e).build();                                  // adds "error" with the exception
        }
    }
}
