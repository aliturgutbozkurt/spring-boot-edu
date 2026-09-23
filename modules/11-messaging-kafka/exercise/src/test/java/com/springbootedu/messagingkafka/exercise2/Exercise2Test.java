package com.springbootedu.messagingkafka.exercise2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.springbootedu.messagingkafka.KafkaTestSupport;
import com.springbootedu.messagingkafka.TestcontainersConfiguration;
import com.springbootedu.messagingkafka.events.OrderPlaced;
import java.time.Duration;
import java.util.List;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;

/**
 * Exercise 2 — poison messages go to the dead letter topic and do not block the messages behind them.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class Exercise2Test {

    @Autowired
    KafkaTemplate<String, OrderPlaced> kafka;

    @Autowired
    PaymentListener payments;

    @Autowired
    KafkaConnectionDetails connection;

    @Test
    void anInvalidOrderGoesToTheDeadLetterTopicWithoutRetries() {
        OrderPlaced invalid = new OrderPlaced(UUID.randomUUID().toString(), "9780134685991", -1);

        kafka.send(PaymentListener.TOPIC, invalid.orderId(), invalid);

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(deadLetters()).anySatisfy(record -> {
                    assertThat(record.key()).isEqualTo(invalid.orderId());
                    assertThat(record.headers().lastHeader(KafkaHeaders.DLT_EXCEPTION_FQCN)).isNotNull();
                }));
        assertThat(payments.attempts(invalid.orderId())).isEqualTo(1);    // not retried: it can never succeed
    }

    @Test
    void aMessageThatIsNotJsonGoesToTheDeadLetterTopic() {
        String key = UUID.randomUUID().toString();
        try (var producer = new KafkaProducer<>(Map.<String, Object>of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                String.join(",", connection.getProducer().getBootstrapServers())),
                new StringSerializer(), new StringSerializer())) {
            producer.send(new ProducerRecord<>(PaymentListener.TOPIC, key, "{ not json"));
        }

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(deadLetters()).anySatisfy(record -> assertThat(record.value()).isEqualTo("{ not json")));
    }

    @Test
    void theMessagesBehindAPoisonPillAreStillProcessed() {
        String key = UUID.randomUUID().toString();                      // same key → same partition, in order
        kafka.send(PaymentListener.TOPIC, key, new OrderPlaced(key, "9780134685991", 0));
        OrderPlaced valid = new OrderPlaced(key, "9780134685991", 2);
        kafka.send(PaymentListener.TOPIC, key, valid);

        await().atMost(Duration.ofSeconds(30)).until(() -> payments.paid().contains(valid));
    }

    private List<ConsumerRecord<String, String>> deadLetters() {
        return KafkaTestSupport.readAll(connection, PaymentListener.TOPIC + ".DLT", "read_uncommitted",
                Duration.ofSeconds(1));
    }
}
