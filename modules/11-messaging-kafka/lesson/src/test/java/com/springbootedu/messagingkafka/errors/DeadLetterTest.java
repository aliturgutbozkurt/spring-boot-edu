package com.springbootedu.messagingkafka.errors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.springbootedu.messagingkafka.KafkaTestSupport;
import com.springbootedu.messagingkafka.TestcontainersConfiguration;
import com.springbootedu.messagingkafka.order.OrderEvents;
import com.springbootedu.messagingkafka.order.OrderPlaced;
import com.springbootedu.messagingkafka.shipping.Shipments;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.support.KafkaHeaders;

/**
 * Lesson 3.3 — a message that can never be processed must not block the partition: it goes to the dead letter topic.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class DeadLetterTest {

    @Autowired
    OrderEvents events;

    @Autowired
    Shipments shipments;

    @Autowired
    KafkaConnectionDetails kafka;

    @Test
    void anInvalidOrderEndsUpInTheDeadLetterTopic() {
        OrderPlaced invalid = new OrderPlaced(UUID.randomUUID().toString(), "customer-1", "9780134685991", 0);
        events.publish(invalid);

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(KafkaTestSupport.readAll(kafka, OrderEvents.DEAD_LETTER_TOPIC, "read_uncommitted",
                        Duration.ofSeconds(1)))
                        .anySatisfy(record -> {
                            assertThat(record.value()).contains(invalid.orderId());
                            assertThat(header(record, KafkaHeaders.DLT_EXCEPTION_MESSAGE)).contains("quantity");
                        }));
        assertThat(shipments.shipped()).doesNotContain(invalid);
    }

    @Test
    void aMessageThatIsNotJsonEndsUpInTheDeadLetterTopic() {
        String key = UUID.randomUUID().toString();
        try (var producer = new KafkaProducer<>(Map.<String, Object>of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, String.join(",", kafka.getProducer().getBootstrapServers())),
                new StringSerializer(), new StringSerializer())) {
            producer.send(new ProducerRecord<>(OrderEvents.TOPIC, key, "this is not json"));   // a poison pill
        }

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(KafkaTestSupport.readAll(kafka, OrderEvents.DEAD_LETTER_TOPIC, "read_uncommitted",
                        Duration.ofSeconds(1)))
                        .anySatisfy(record -> {
                            assertThat(record.key()).isEqualTo(key);
                            assertThat(record.value()).isEqualTo("this is not json");     // the original bytes
                        }));
    }

    private static String header(ConsumerRecord<String, String> record, String name) {
        var header = record.headers().lastHeader(name);
        return header == null ? "" : new String(header.value(), StandardCharsets.UTF_8);
    }
}
