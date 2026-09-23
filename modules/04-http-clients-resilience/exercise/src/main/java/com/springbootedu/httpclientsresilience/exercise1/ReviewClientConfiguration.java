package com.springbootedu.httpclientsresilience.exercise1;

import org.springframework.context.annotation.Configuration;

/**
 * Exercise 1 — registers {@link ReviewApi} in the "reviews" group (base URL: spring.http.serviceclient.reviews.base-url).
 */
@Configuration(proxyBeanMethods = false)
// TODO 1d: register ReviewApi as an HTTP service client in the group "reviews"
public class ReviewClientConfiguration {
}
