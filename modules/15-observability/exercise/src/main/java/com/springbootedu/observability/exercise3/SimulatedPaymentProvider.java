package com.springbootedu.observability.exercise3;

import java.time.Duration;
import org.springframework.stereotype.Component;

/**
 * Given: a stand-in for the real provider, always healthy.
 */
@Component
class SimulatedPaymentProvider implements PaymentProvider {

    @Override
    public ProviderStatus ping() {
        return new ProviderStatus(Duration.ofMillis(80), false);
    }
}
