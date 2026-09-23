package com.springbootedu.configuration.store;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;

/**
 * Lesson 3.2 — typed, validated configuration.
 */
class StorePropertiesTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
            .withUserConfiguration(StoreConfiguration.class)
            .withPropertyValues(
                    "bookstore.store.name=Kitapçı",
                    "bookstore.store.support-email=destek@kitapci.example",
                    "bookstore.store.categories=roman,bilim",
                    "bookstore.store.shipping.free-from=500",
                    "bookstore.store.shipping.delivery-time=2d");

    @Test
    void bindsNestedValuesListsDurationsAndDefaults() {
        runner.run(context -> {
            var store = context.getBean(StoreProperties.class);
            assertThat(store.categories()).containsExactly("roman", "bilim");
            assertThat(store.shipping().freeFrom()).isEqualByComparingTo("500");
            assertThat(store.shipping().deliveryTime()).isEqualTo(Duration.ofDays(2));
            assertThat(store.currency()).isEqualTo(Currency.getInstance("TRY"));   // @DefaultValue
        });
    }

    @Test
    void aBlankNameStopsTheApplication() {
        runner.withPropertyValues("bookstore.store.name=")
                .run(context -> assertThat(context).hasFailed()
                        .getFailure().rootCause().isInstanceOf(BindValidationException.class)
                        .hasMessageContaining("name"));
    }

    @Test
    void nestedObjectsAreValidatedToo() {
        runner.withPropertyValues("bookstore.store.shipping.free-from=-1")
                .run(context -> assertThat(context).hasFailed()
                        .getFailure().rootCause().hasMessageContaining("shipping.freeFrom"));
    }

    @Test
    void anInvalidEmailIsRejected() {
        runner.withPropertyValues("bookstore.store.support-email=not-an-email")
                .run(context -> assertThat(context).hasFailed());
    }
}
