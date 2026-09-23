package com.springbootedu.corecontainer.configuration;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Lesson 3.2 — {@code @Bean} for classes we do not own and therefore cannot annotate with {@code @Component}.
 */
// tag::bean-method[]
@Configuration(proxyBeanMethods = false)
public class AppConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();   // java.time.Clock: a JDK class, no @Component possible
    }
}
// end::bean-method[]
