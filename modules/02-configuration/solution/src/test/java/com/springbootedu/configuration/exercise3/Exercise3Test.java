package com.springbootedu.configuration.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Exercise 3 — your own auto-configuration.
 */
class Exercise3Test {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ExchangeRateAutoConfiguration.class))
            .withPropertyValues("bookstore.exchange.rates.USD=0.025", "bookstore.exchange.rates.EUR=0.023");

    @Test
    void convertsWithTheConfiguredRates() {
        runner.run(context -> {
            var exchange = context.getBean(ExchangeRateService.class);
            assertThat(exchange.convert(new BigDecimal("1000"), "USD")).isEqualByComparingTo("25.00");
            assertThat(exchange.convert(new BigDecimal("1000"), "EUR")).isEqualByComparingTo("23.00");
        });
    }

    @Test
    void theBaseCurrencyDefaultsToTry() {
        runner.run(context -> assertThat(context.getBean(ExchangeRateProperties.class).base()).isEqualTo("TRY"));
    }

    @Test
    void backsOffWhenTheApplicationHasItsOwnService() {
        runner.withUserConfiguration(OwnExchange.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(ExchangeRateService.class);
                    assertThat(context.getBean(ExchangeRateService.class).convert(BigDecimal.TEN, "USD"))
                            .isEqualByComparingTo("1");
                });
    }

    @Test
    void canBeDisabled() {
        runner.withPropertyValues("bookstore.exchange.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(ExchangeRateService.class));
    }

    @Test
    void isRegisteredForAutoConfiguration() throws IOException {
        var imports = new ClassPathResource(
                "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        assertThat(imports.getContentAsString(StandardCharsets.UTF_8))
                .contains(ExchangeRateAutoConfiguration.class.getName());
    }

    @Configuration(proxyBeanMethods = false)
    static class OwnExchange {

        @Bean
        ExchangeRateService ownExchangeRateService() {
            return (amount, currency) -> BigDecimal.ONE;
        }
    }
}
