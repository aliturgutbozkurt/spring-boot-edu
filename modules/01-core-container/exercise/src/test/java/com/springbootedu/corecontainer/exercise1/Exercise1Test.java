package com.springbootedu.corecontainer.exercise1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Exercise 1 — strategy pattern with Map injection.
 */
class Exercise1Test {

    @Configuration(proxyBeanMethods = false)
    @ComponentScan(basePackageClasses = ShippingService.class)
    static class ScanExercise1 {
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(ScanExercise1.class);

    @Test
    void storePickupIsRegisteredAsTheBeanNamedPickup() {
        runner.run(context -> assertThat(context.getBean("pickup")).isInstanceOf(StorePickupShipping.class));
    }

    @Test
    void listsEveryShippingMethodSorted() {
        runner.run(context -> assertThat(context.getBean(ShippingService.class).availableMethods())
                .containsExactly("express", "pickup", "standard"));
    }

    @Test
    void usesTheCalculatorThatMatchesTheMethodName() {
        runner.run(context -> {
            var shipping = context.getBean(ShippingService.class);
            assertThat(shipping.cost("standard", new BigDecimal("100.00"))).isEqualByComparingTo("29.90");
            assertThat(shipping.cost("standard", new BigDecimal("600.00"))).isEqualByComparingTo("0.00");
            assertThat(shipping.cost("express", new BigDecimal("100.00"))).isEqualByComparingTo("59.90");
            assertThat(shipping.cost("pickup", new BigDecimal("100.00"))).isEqualByComparingTo("0.00");
        });
    }

    @Test
    void rejectsAnUnknownMethodAndNamesTheValidOnes() {
        runner.run(context -> assertThatIllegalArgumentException()
                .isThrownBy(() -> context.getBean(ShippingService.class).cost("drone", BigDecimal.TEN))
                .withMessageContaining("drone")
                .withMessageContaining("[express, pickup, standard]"));
    }
}
