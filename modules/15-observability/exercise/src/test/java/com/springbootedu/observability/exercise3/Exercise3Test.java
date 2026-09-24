package com.springbootedu.observability.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

/**
 * Exercise 3 — a health check for the payment provider.
 */
class Exercise3Test {

    private static Health healthWith(PaymentProvider provider) {
        return new PaymentProviderHealthIndicator(provider).health();
    }

    @Test
    void aFastAnswerIsUpWithTheResponseTime() {
        Health health = healthWith(new FakeProvider(Duration.ofMillis(120), false, false));

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("responseTimeMs", 120L);
    }

    @Test
    void aSlowAnswerIsDown() {
        Health health = healthWith(new FakeProvider(Duration.ofMillis(900), false, false));

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("reason", "slower than 500 ms");
    }

    @Test
    void anErrorIsDownWithTheError() {
        Health health = healthWith(new FakeProvider(Duration.ZERO, false, true));

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsKey("error");
    }

    @Test
    void maintenanceIsOutOfService() {
        Health health = healthWith(new FakeProvider(Duration.ofMillis(50), true, false));

        assertThat(health.getStatus()).isEqualTo(Status.OUT_OF_SERVICE);
    }

    record FakeProvider(Duration responseTime, boolean maintenance, boolean failing) implements PaymentProvider {

        @Override
        public ProviderStatus ping() {
            if (failing) {
                throw new IllegalStateException("connection refused");
            }
            return new ProviderStatus(responseTime, maintenance);
        }
    }
}
