package com.springbootedu.messagingkafka.outbox;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Lesson 3.6 — switches on {@code @Scheduled} for the outbox relay.
 */
@Configuration(proxyBeanMethods = false)
@EnableScheduling
class SchedulingConfiguration {
}
