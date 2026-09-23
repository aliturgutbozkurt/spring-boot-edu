package com.springbootedu.configuration.exercise3;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Exercise 3 — auto-configures an {@link ExchangeRateService} unless the application has one.
 */
@AutoConfiguration
@ConditionalOnBooleanProperty(name = "bookstore.exchange.enabled", matchIfMissing = true)
@EnableConfigurationProperties(ExchangeRateProperties.class)
public class ExchangeRateAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    ExchangeRateService exchangeRateService(ExchangeRateProperties properties) {
        return new FixedExchangeRateService(properties.rates());
    }
}
