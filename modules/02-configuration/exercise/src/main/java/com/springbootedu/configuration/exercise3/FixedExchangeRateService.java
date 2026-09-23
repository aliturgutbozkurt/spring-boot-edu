package com.springbootedu.configuration.exercise3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Exercise 3 — given: an {@link ExchangeRateService} backed by fixed rates.
 */
public class FixedExchangeRateService implements ExchangeRateService {

    private final Map<String, BigDecimal> rates;

    public FixedExchangeRateService(Map<String, BigDecimal> rates) {
        this.rates = Map.copyOf(rates);
    }

    @Override
    public BigDecimal convert(BigDecimal amount, String currency) {
        BigDecimal rate = rates.get(currency);
        if (rate == null) {
            throw new IllegalArgumentException("No rate for " + currency + ", known: " + rates.keySet());
        }
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
