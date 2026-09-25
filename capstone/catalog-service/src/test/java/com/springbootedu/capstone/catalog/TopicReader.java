package com.springbootedu.capstone.catalog;

import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails;

/**
 * Reads a topic from the beginning, as raw JSON strings — the way any consumer in any language would see it.
 */
public final class TopicReader {

    private TopicReader() {
    }

    /** Waits until the topic has a record with this key and returns its values (in order). */
    public static List<String> valuesWithKey(KafkaConnectionDetails kafka, String topic, String key) {
        return valuesWithKey(kafka, topic, key, 1);
    }

    /** Waits until the topic has at least {@code count} records with this key and returns their values. */
    public static List<String> valuesWithKey(KafkaConnectionDetails kafka, String topic, String key, int count) {
        Map<String, Object> config = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getConsumer().getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "test-" + UUID.randomUUID(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        try (var consumer = new KafkaConsumer<>(config, new StringDeserializer(), new StringDeserializer())) {
            consumer.subscribe(List.of(topic));
            List<String> values = new ArrayList<>();
            await().atMost(Duration.ofSeconds(30)).until(() -> {
                for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(200))) {
                    if (key.equals(record.key())) {
                        values.add(record.value());
                    }
                }
                return values.size() >= count;
            });
            return values;
        }
    }
}
