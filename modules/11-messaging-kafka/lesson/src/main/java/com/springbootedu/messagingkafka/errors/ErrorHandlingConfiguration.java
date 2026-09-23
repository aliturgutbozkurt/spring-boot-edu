package com.springbootedu.messagingkafka.errors;

import com.springbootedu.messagingkafka.order.OrderEvents;
import com.springbootedu.messagingkafka.shipping.InvalidOrderException;
import java.util.Map;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Lesson 3.3 — what happens when a listener throws: retry a few times, then park the record in the dead letter topic.
 */
// tag::error-handler[]
@Configuration(proxyBeanMethods = false)
public class ErrorHandlingConfiguration {

    @Bean                                               // Boot adds a CommonErrorHandler bean to every listener
    DefaultErrorHandler kafkaErrorHandler(ProducerFactory<Object, Object> producers) {
        var recoverer = new DeadLetterPublishingRecoverer(deadLetterTemplate(producers),
                (record, exception) -> new TopicPartition(OrderEvents.DEAD_LETTER_TOPIC, -1));   // -1: any partition
        var handler = new DefaultErrorHandler(recoverer, new FixedBackOff(200, 2));  // 1 try + 2 retries, 200 ms apart
        handler.addNotRetryableExceptions(InvalidOrderException.class);             // hopeless: straight to the DLT
        return handler;
    }
    // end::error-handler[]

    /**
     * A record that could not be deserialized is republished as its original bytes; everything else as JSON.
     */
    private static KafkaTemplate<String, Object> deadLetterTemplate(ProducerFactory<Object, Object> producers) {
        var values = new DelegatingByTypeSerializer(Map.of(
                byte[].class, new ByteArraySerializer(),
                Object.class, new JacksonJsonSerializer<>()), true);             // true: match subclasses too
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producers.getConfigurationProperties(),
                new StringSerializer(), values));
    }
}
