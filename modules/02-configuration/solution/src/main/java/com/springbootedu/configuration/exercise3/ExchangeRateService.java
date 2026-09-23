package com.springbootedu.configuration.exercise3;

import java.math.BigDecimal;

/**
 * Exercise 3 — given: converts an amount in the base currency into another currency.
 */
@FunctionalInterface
public interface ExchangeRateService {

    BigDecimal convert(BigDecimal amount, String currency);
}
