package com.springbootedu.configuration.exercise1;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Exercise 1 — given: registers {@link OpeningHoursProperties}.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OpeningHoursProperties.class)
public class OpeningHoursConfiguration {
}
