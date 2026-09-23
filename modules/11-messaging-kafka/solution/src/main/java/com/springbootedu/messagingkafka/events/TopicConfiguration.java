package com.springbootedu.messagingkafka.events;

import com.springbootedu.messagingkafka.exercise1.StockUpdater;
import com.springbootedu.messagingkafka.exercise2.PaymentListener;
import com.springbootedu.messagingkafka.exercise3.OutboxRelay;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Given: the topics of the exercises.
 */
@Configuration(proxyBeanMethods = false)
class TopicConfiguration {

    @Bean
    NewTopic stockTopic() {
        return TopicBuilder.name(StockUpdater.TOPIC).partitions(3).build();
    }

    @Bean
    NewTopic paymentsTopic() {
        return TopicBuilder.name(PaymentListener.TOPIC).partitions(3).build();
    }

    @Bean
    NewTopic paymentsDeadLetterTopic() {
        return TopicBuilder.name(PaymentListener.TOPIC + ".DLT").partitions(1).build();
    }

    @Bean
    NewTopic outboxTopic() {
        return TopicBuilder.name(OutboxRelay.TOPIC).partitions(1).build();     // one partition: one order
    }
}
