package com.springbootedu.messagingkafka.exercise2;

import java.util.Map;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

/**
 * Given: a template that can republish both the original bytes of a poison pill and normal objects as JSON.
 */
public final class DeadLetterTemplates {

    private DeadLetterTemplates() {
    }

    public static KafkaTemplate<String, Object> create(ProducerFactory<Object, Object> producers) {
        var values = new DelegatingByTypeSerializer(Map.of(
                byte[].class, new ByteArraySerializer(),
                Object.class, new JacksonJsonSerializer<>()), true);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producers.getConfigurationProperties(),
                new StringSerializer(), values));
    }
}
