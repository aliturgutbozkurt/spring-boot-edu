package com.springbootedu.corecontainer.configuration;

import java.math.BigDecimal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Lesson 3.2 — the same configuration in "full" and "lite" mode.
 */
public final class ProxyModeExamples {

    private ProxyModeExamples() {
    }

    // tag::full-mode[]
    @Configuration                                     // proxyBeanMethods = true (default)
    static class FullModeConfiguration {

        @Bean
        TaxRate fullModeTaxRate() {
            return new TaxRate(new BigDecimal("0.20"));
        }

        @Bean
        PriceCalculator fullModePriceCalculator() {
            // The CGLIB proxy intercepts this call and returns the existing singleton bean.
            return new PriceCalculator(fullModeTaxRate());
        }
    }
    // end::full-mode[]

    // tag::lite-mode[]
    @Configuration(proxyBeanMethods = false)           // no proxy: faster startup, plain Java calls
    static class LiteModeConfiguration {

        @Bean
        TaxRate liteModeTaxRate() {
            return new TaxRate(new BigDecimal("0.20"));
        }

        @Bean
        PriceCalculator liteModePriceCalculator() {
            // PITFALL: without a proxy this is a plain method call → a second, unmanaged TaxRate.
            return new PriceCalculator(liteModeTaxRate());
        }

        @Bean
        PriceCalculator liteModeSafePriceCalculator(TaxRate liteModeTaxRate) {
            // Correct lite-mode style: receive the bean as a method parameter.
            return new PriceCalculator(liteModeTaxRate);
        }
    }
    // end::lite-mode[]
}
