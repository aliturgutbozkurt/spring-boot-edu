package com.springbootedu.httpclientsresilience.resilience;

import org.springframework.context.annotation.Configuration;
import org.springframework.resilience.annotation.EnableResilientMethods;

/**
 * Lessons 3.6–3.7 — switches on @Retryable and @ConcurrencyLimit (Spring Framework 7, no extra library).
 */
// tag::enable[]
@Configuration(proxyBeanMethods = false)
@EnableResilientMethods
public class ResilienceConfiguration {
}
// end::enable[]
