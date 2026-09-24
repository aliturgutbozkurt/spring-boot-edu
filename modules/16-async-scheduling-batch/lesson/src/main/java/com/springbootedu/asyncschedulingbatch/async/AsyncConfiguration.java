package com.springbootedu.asyncschedulingbatch.async;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Lesson 3.1 — switches @Async on. The executor is Boot's applicationTaskExecutor: with
 * spring.threads.virtual.enabled=true it starts one virtual thread per task.
 */
@Configuration(proxyBeanMethods = false)
@EnableAsync
class AsyncConfiguration {
}
