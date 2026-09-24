package com.springbootedu.asyncschedulingbatch.exercise3;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Given: the clock of the application, in the shop's time zone. Tests replace it.
 */
@Configuration(proxyBeanMethods = false)
class ClockConfiguration {

    @Bean
    Clock clock() {
        return Clock.system(ZoneId.of("Europe/Istanbul"));
    }
}
