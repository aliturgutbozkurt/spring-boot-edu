package com.springbootedu.asyncschedulingbatch.exercise1;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Given: @Async and @Scheduled are switched on (virtual threads: see application.yaml).
 */
@Configuration(proxyBeanMethods = false)
@EnableAsync
@EnableScheduling
class AsyncConfiguration {
}
