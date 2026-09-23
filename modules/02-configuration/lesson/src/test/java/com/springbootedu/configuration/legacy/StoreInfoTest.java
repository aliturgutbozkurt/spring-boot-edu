package com.springbootedu.configuration.legacy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Lesson 3.3 — @Value: fine for one value, fragile for many.
 */
class StoreInfoTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(PropertyPlaceholderAutoConfiguration.class))
            .withBean(StoreInfo.class);

    @Test
    void injectsAValueAndUsesTheDefaultForAMissingOne() {
        runner.withPropertyValues("bookstore.store.name=Kitapçı")
                .run(context -> assertThat(context.getBean(StoreInfo.class).describe())
                        .isEqualTo("Kitapçı · +90 212 000 00 00 · max 5 kitap / books"));
    }

    @Test
    void aMissingValueWithoutADefaultFailsAtStartup() {
        runner.run(context -> assertThat(context).hasFailed()
                .getFailure().rootCause().hasMessageContaining("bookstore.store.name"));
    }
}
