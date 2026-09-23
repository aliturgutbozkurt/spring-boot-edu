package com.springbootedu.corecontainer.scope;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Lesson 3.3 — a prototype bean injected into a singleton is created only once.
 */
class CheckoutServiceTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withBean(ShoppingCart.class)
            .withBean(BrokenCheckoutService.class)
            .withBean(CheckoutService.class);

    @Test
    void prototypeScopeGivesANewCartOnEveryLookup() {
        runner.run(context -> assertThat(context.getBean(ShoppingCart.class))
                .isNotSameAs(context.getBean(ShoppingCart.class)));
    }

    @Test
    void injectingAPrototypeIntoASingletonSharesOneCart() {
        runner.run(context -> {
            var service = context.getBean(BrokenCheckoutService.class);
            assertThat(service.startCheckout()).isSameAs(service.startCheckout());
        });
    }

    @Test
    void objectProviderGivesEveryCheckoutItsOwnCart() {
        runner.run(context -> {
            var service = context.getBean(CheckoutService.class);
            ShoppingCart first = service.startCheckout();
            first.add("978-0-13-468599-1");
            assertThat(service.startCheckout()).isNotSameAs(first);
            assertThat(service.startCheckout().items()).isEmpty();
        });
    }
}
