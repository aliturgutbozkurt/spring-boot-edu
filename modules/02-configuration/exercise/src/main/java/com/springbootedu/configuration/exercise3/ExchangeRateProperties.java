package com.springbootedu.configuration.exercise3;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Exercise 3 — given: "bookstore.exchange.*" settings.
 *
 * @param enabled whether the auto-configuration creates an {@link ExchangeRateService}
 * @param base    currency the amounts are in
 * @param rates   1 unit of {@code base} = rate units of the target currency
 */
@ConfigurationProperties("bookstore.exchange")
public record ExchangeRateProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("TRY") String base,
        @DefaultValue Map<String, BigDecimal> rates) {
}
