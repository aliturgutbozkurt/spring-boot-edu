package com.springbootedu.configuration.exercise3;

/**
 * Exercise 3 — auto-configures an {@link ExchangeRateService} unless the application has one.
 */
// TODO 3a: turn this class into an auto-configuration
// TODO 3b: apply only when bookstore.exchange.enabled is true or missing
// TODO 3c: bind ExchangeRateProperties
public class ExchangeRateAutoConfiguration {

    // TODO 3d: a FixedExchangeRateService bean built from the configured rates,
    //          created only when the application does not define its own ExchangeRateService
}
