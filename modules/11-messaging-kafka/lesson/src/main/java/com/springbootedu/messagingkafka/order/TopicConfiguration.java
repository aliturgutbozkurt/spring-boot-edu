package com.springbootedu.messagingkafka.order;

import com.springbootedu.messagingkafka.transactions.StockTransfers;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Lesson 3.1 — NewTopic beans: Boot's KafkaAdmin creates the topics on startup if they do not exist.
 */
// tag::topics[]
@Configuration(proxyBeanMethods = false)
public class TopicConfiguration {

    @Bean
    NewTopic ordersTopic() {
        return TopicBuilder.name(OrderEvents.TOPIC).partitions(3).replicas(1).build();   // 3 = max. parallel consumers
    }

    @Bean
    NewTopic ordersDeadLetterTopic() {
        return TopicBuilder.name(OrderEvents.DEAD_LETTER_TOPIC).partitions(1).replicas(1).build();
    }
    // end::topics[]

    @Bean
    NewTopic stockTransfersTopic() {
        return TopicBuilder.name(StockTransfers.TOPIC).partitions(1).replicas(1).build();
    }
}
