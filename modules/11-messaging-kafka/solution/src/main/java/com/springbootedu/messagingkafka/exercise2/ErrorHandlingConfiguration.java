package com.springbootedu.messagingkafka.exercise2;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Exercise 2 — retry briefly, then send the record to "&lt;topic&gt;.DLT".
 */
@Configuration(proxyBeanMethods = false)
public class ErrorHandlingConfiguration {

    @Bean
    DefaultErrorHandler kafkaErrorHandler(ProducerFactory<Object, Object> producers) {
        var recoverer = new DeadLetterPublishingRecoverer(DeadLetterTemplates.create(producers),
                (record, exception) -> new TopicPartition(record.topic() + ".DLT", -1));
        var handler = new DefaultErrorHandler(recoverer, new FixedBackOff(100, 2));
        handler.addNotRetryableExceptions(InvalidQuantityException.class);
        return handler;
    }
}
