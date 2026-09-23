package com.springbootedu.httpclientsresilience.exercise1;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

/**
 * Exercise 1 — registers {@link ReviewApi} in the "reviews" group (base URL: spring.http.serviceclient.reviews.base-url).
 */
@Configuration(proxyBeanMethods = false)
@ImportHttpServices(group = "reviews", types = ReviewApi.class)
public class ReviewClientConfiguration {
}
