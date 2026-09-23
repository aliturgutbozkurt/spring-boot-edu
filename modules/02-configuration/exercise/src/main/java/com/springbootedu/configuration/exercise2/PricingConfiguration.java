package com.springbootedu.configuration.exercise2;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Exercise 2 — given: the application the tests start with different profiles.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PricingProperties.class)
public class PricingConfiguration {
}
