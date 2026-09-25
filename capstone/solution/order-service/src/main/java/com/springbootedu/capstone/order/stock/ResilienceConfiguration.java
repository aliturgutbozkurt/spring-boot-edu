package com.springbootedu.capstone.order.stock;

import org.springframework.context.annotation.Configuration;
import org.springframework.resilience.annotation.EnableResilientMethods;

/**
 * Exercise 3 — switches on {@code @Retryable} (Spring Framework 7, module 04).
 */
@Configuration(proxyBeanMethods = false)
@EnableResilientMethods
class ResilienceConfiguration {
}
