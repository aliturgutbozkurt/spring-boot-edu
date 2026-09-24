package com.springbootedu.modulith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.springbootedu.modulith.order.Order;
import com.springbootedu.modulith.order.OrderService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Lesson 3.4 — an @Externalized event also leaves the application: it is sent to a Kafka topic.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class ExternalizationTest {

    @Autowired
    OrderService orders;

    @Test
    void orderPlacedIsPublishedToKafkaWithTheCustomerAsKey() {
        Map<String, Object> config = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, TestcontainersConfiguration.KAFKA.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "externalization-test",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        try (var consumer = new KafkaConsumer<>(config, new StringDeserializer(), new StringDeserializer())) {
            consumer.subscribe(List.of("bookstore.orders"));

            Order order = orders.place("c-4", "9781449373320", 1);

            List<ConsumerRecord<String, String>> received = new ArrayList<>();
            await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
                consumer.poll(Duration.ofMillis(500)).forEach(received::add);
                assertThat(received).anySatisfy(record -> {
                    assertThat(record.key()).isEqualTo("c-4");
                    assertThat(record.value()).contains("\"orderId\":" + order.id());
                });
            });
        }
    }
}
