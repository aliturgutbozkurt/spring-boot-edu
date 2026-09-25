package com.springbootedu.capstone.search.index;

import com.springbootedu.capstone.contracts.events.BookChanged;
import com.springbootedu.capstone.contracts.events.OrderPlaced;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

/**
 * ADR-4 — the read model is fed only by events. The messages are plain JSON; each listener names the record
 * it expects. A failing message is retried by Spring Kafka's default error handler.
 */
@Component
class IndexListeners {

    private final BookIndex index;

    IndexListeners(BookIndex index) {
        this.index = index;
    }

    @KafkaListener(topics = BookChanged.TOPIC, groupId = "search-books",
            properties = "spring.json.value.default.type=com.springbootedu.capstone.contracts.events.BookChanged")
    void onBookChanged(BookChanged book) {
        index.index(book);
    }

    @KafkaListener(topics = OrderPlaced.TOPIC, groupId = "search-sales",
            properties = "spring.json.value.default.type=com.springbootedu.capstone.contracts.events.OrderPlaced")
    void onOrderPlaced(OrderPlaced order) {
        index.recordSale(order);
    }

    /** Declared here too, so the listeners never wait for a topic the producer has not created yet. */
    @Configuration(proxyBeanMethods = false)
    static class Topics {

        @Bean
        NewTopic catalogTopic() {
            return TopicBuilder.name(BookChanged.TOPIC).partitions(3).build();
        }

        @Bean
        NewTopic ordersTopic() {
            return TopicBuilder.name(OrderPlaced.TOPIC).partitions(3).build();
        }
    }
}
