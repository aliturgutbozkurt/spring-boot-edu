package com.springbootedu.httpclientsresilience;

import org.springframework.context.annotation.Configuration;
import org.springframework.resilience.annotation.EnableResilientMethods;

/**
 * Exercises 2 and 3 — switches on @Retryable and @ConcurrencyLimit.
 */
@Configuration(proxyBeanMethods = false)
@EnableResilientMethods
public class ResilienceConfiguration {
}
