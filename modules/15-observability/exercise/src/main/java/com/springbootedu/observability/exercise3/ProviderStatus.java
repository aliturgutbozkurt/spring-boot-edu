package com.springbootedu.observability.exercise3;

import java.time.Duration;

/**
 * Given: what the payment provider's ping answers.
 */
public record ProviderStatus(Duration responseTime, boolean maintenance) {
}
