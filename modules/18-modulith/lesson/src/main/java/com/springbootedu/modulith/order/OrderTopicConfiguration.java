package com.springbootedu.modulith.order;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Lesson 3.4 — the topic of the externalized OrderPlaced events, created at startup by Boot's KafkaAdmin.
 */
@Configuration(proxyBeanMethods = false)
class OrderTopicConfiguration {

    @Bean
    NewTopic ordersTopic() {
        return TopicBuilder.name("bookstore.orders").partitions(3).build();
    }
}
