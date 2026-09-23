package com.springbootedu.configuration.store;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Lesson 3.2 — registers the properties record as a bean (alternative: @ConfigurationPropertiesScan).
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(StoreProperties.class)
public class StoreConfiguration {
}
