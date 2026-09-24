package com.springbootedu.testing.order;

import com.springbootedu.testing.pricing.PriceCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Lesson 3.1 — the pure PriceCalculator becomes a bean here, so the class itself stays free of Spring.
 */
@Configuration(proxyBeanMethods = false)
class OrderConfiguration {

    @Bean
    PriceCalculator priceCalculator() {
        return new PriceCalculator();
    }
}
