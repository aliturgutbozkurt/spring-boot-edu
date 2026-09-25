package com.springbootedu.capstone.order.outbox;

import com.springbootedu.capstone.contracts.events.OrderPlaced;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * The order service owns the topic of its events: it creates it at startup (a no-op if it exists).
 */
@Configuration(proxyBeanMethods = false)
class TopicConfiguration {

    @Bean
    NewTopic ordersTopic() {
        return TopicBuilder.name(OrderPlaced.TOPIC).partitions(3).build();   // key = order ID keeps an order's events in order
    }
}
