package com.springbootedu.dockerdeployment.shop;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;

/**
 * Lesson 3.5 — environment variables bind to @ConfigurationProperties (relaxed binding).
 */
class ShopPropertiesTest {

    @Test
    void environmentVariablesSetTheProperties() {
        ShopProperties shop = bind(Map.of("BOOKSTORE_SHOP_NAME", "Ankara", "BOOKSTORE_SHOP_CURRENCY", "EUR"));

        assertThat(shop.name()).isEqualTo("Ankara");
        assertThat(shop.currency()).isEqualTo("EUR");
    }

    @Test
    void withoutVariablesTheDefaultsApply() {
        ShopProperties shop = bind(Map.of());

        assertThat(shop.name()).isEqualTo("Bookstore");
        assertThat(shop.currency()).isEqualTo("TRY");
    }

    private static ShopProperties bind(Map<String, Object> environmentVariables) {
        var environment = new StandardEnvironment();
        MutablePropertySources sources = environment.getPropertySources();
        sources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, new SystemEnvironmentPropertySource(
                StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, environmentVariables));
        return new Binder(ConfigurationPropertySources.get(environment))
                .bindOrCreate("bookstore.shop", ShopProperties.class);
    }
}
