package com.springbootedu.corecontainer.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Exercise 2 — a feature flag with conditional beans.
 */
class Exercise2Test {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(GiftWrapConfiguration.class);

    @Test
    void giftWrapIsOffWhenThePropertyIsMissing() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(GiftWrapService.class);
            assertThat(context.getBean(GiftWrapService.class)).isInstanceOf(NoGiftWrap.class);
        });
    }

    @Test
    void giftWrapIsOffWhenDisabled() {
        runner.withPropertyValues("bookstore.gift-wrap.enabled=false")
                .run(context -> assertThat(context.getBean(GiftWrapService.class)).isInstanceOf(NoGiftWrap.class));
    }

    @Test
    void paidGiftWrapWithTheDefaultPriceWhenEnabled() {
        runner.withPropertyValues("bookstore.gift-wrap.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(GiftWrapService.class);
                    var giftWrap = context.getBean(GiftWrapService.class);
                    assertThat(giftWrap).isInstanceOf(PaidGiftWrap.class);
                    assertThat(giftWrap.price()).isEqualByComparingTo("15.00");
                });
    }

    @Test
    void thePriceIsConfigurable() {
        runner.withPropertyValues("bookstore.gift-wrap.enabled=true", "bookstore.gift-wrap.price=22.50")
                .run(context -> assertThat(context.getBean(GiftWrapService.class).price()).isEqualByComparingTo("22.50"));
    }
}
