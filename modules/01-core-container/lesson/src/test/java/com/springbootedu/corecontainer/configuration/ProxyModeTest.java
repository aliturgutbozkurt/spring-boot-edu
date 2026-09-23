package com.springbootedu.corecontainer.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Lesson 3.2 — {@code @Configuration} classes are proxied (full mode) unless proxyBeanMethods = false (lite mode).
 */
class ProxyModeTest {

    @Test
    void fullModeReturnsTheSingletonWhenABeanMethodIsCalledDirectly() {
        new ApplicationContextRunner()
                .withUserConfiguration(ProxyModeExamples.FullModeConfiguration.class)
                .run(context -> {
                    TaxRate bean = context.getBean("fullModeTaxRate", TaxRate.class);
                    PriceCalculator calculator = context.getBean("fullModePriceCalculator", PriceCalculator.class);
                    assertThat(calculator.taxRate()).isSameAs(bean);
                });
    }

    @Test
    void liteModeCreatesANewObjectWhenABeanMethodIsCalledDirectly() {
        new ApplicationContextRunner()
                .withUserConfiguration(ProxyModeExamples.LiteModeConfiguration.class)
                .run(context -> {
                    TaxRate bean = context.getBean("liteModeTaxRate", TaxRate.class);
                    PriceCalculator calculator = context.getBean("liteModePriceCalculator", PriceCalculator.class);
                    assertThat(calculator.taxRate()).isNotSameAs(bean).isEqualTo(bean);
                });
    }

    @Test
    void liteModeSharesTheBeanWhenItIsInjectedAsAParameter() {
        new ApplicationContextRunner()
                .withUserConfiguration(ProxyModeExamples.LiteModeConfiguration.class)
                .run(context -> {
                    TaxRate bean = context.getBean("liteModeTaxRate", TaxRate.class);
                    PriceCalculator calculator = context.getBean("liteModeSafePriceCalculator", PriceCalculator.class);
                    assertThat(calculator.taxRate()).isSameAs(bean);
                });
    }

    @Test
    void priceIncludesTax() {
        var calculator = new PriceCalculator(new TaxRate(new java.math.BigDecimal("0.10")));
        assertThat(calculator.grossPrice(new java.math.BigDecimal("100.00"))).isEqualByComparingTo("110.00");
    }
}
