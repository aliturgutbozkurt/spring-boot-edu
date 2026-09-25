package com.springbootedu.capstone.catalog.stock;

import com.hazelcast.client.config.ClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ADR-6 — the catalog is a client of the Hazelcast cluster that holds the stock locks. A {@link ClientConfig}
 * bean (not a hazelcast-client.yaml) because Hazelcast's own placeholders cannot read environment variables.
 * In the tests, the Testcontainers service connection replaces this configuration.
 */
@Configuration(proxyBeanMethods = false)
class HazelcastClientSettings {

    @Bean
    ClientConfig hazelcastClientConfig(@Value("${bookstore.hazelcast.address}") String address) {
        ClientConfig config = new ClientConfig();
        config.setClusterName("bookstore");
        config.getNetworkConfig().addAddress(address);
        return config;
    }
}
