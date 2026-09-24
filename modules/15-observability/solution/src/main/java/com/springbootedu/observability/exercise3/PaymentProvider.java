package com.springbootedu.observability.exercise3;

/**
 * Given: the external payment provider. {@code ping()} throws if the provider cannot be reached.
 */
public interface PaymentProvider {

    ProviderStatus ping();
}
