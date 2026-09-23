package com.springbootedu.configuration.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Exercise 2 — prices per profile, and a profile group whose order matters.
 */
class Exercise2Test {

    private static ConfigurableApplicationContext start(String... profiles) {
        return new SpringApplicationBuilder(PricingConfiguration.class)
                .web(WebApplicationType.NONE)
                .profiles(profiles)
                .run();
    }

    @Test
    void defaultsComeFromApplicationYaml() {
        try (var context = start()) {
            var pricing = context.getBean(PricingProperties.class);
            assertThat(pricing.shippingFee()).isEqualByComparingTo("29.90");
            assertThat(pricing.discountPercent()).isZero();
        }
    }

    @Test
    void campaignProfileGivesFreeShippingAndADiscount() {
        try (var context = start("campaign")) {
            var pricing = context.getBean(PricingProperties.class);
            assertThat(pricing.shippingFee()).isEqualByComparingTo("0.00");
            assertThat(pricing.discountPercent()).isEqualTo(20);
        }
    }

    @Test
    void blackFridayGroupCombinesCampaignAndExpressAndExpressWinsTheFee() {
        try (var context = start("black-friday")) {
            var pricing = context.getBean(PricingProperties.class);
            assertThat(context.getEnvironment().getActiveProfiles()).contains("campaign", "express");
            assertThat(pricing.discountPercent()).isEqualTo(20);              // from campaign
            assertThat(pricing.shippingFee()).isEqualByComparingTo("49.90");  // express comes later → wins
        }
    }

    @Test
    void totalAppliesDiscountAndAddsShipping() {
        try (var context = start("campaign")) {
            assertThat(context.getBean(PricingProperties.class).total(new java.math.BigDecimal("100.00")))
                    .isEqualByComparingTo("80.00");
        }
    }
}
