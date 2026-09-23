package com.springbootedu.rediscaching.exercise3;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Given: the application's clock. Tests replace it with a clock they can move forward.
 */
@Configuration(proxyBeanMethods = false)
public class ClockConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
