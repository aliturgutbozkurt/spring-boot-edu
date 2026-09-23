package com.springbootedu.messagingkafka.exercise1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.springbootedu.messagingkafka.TestcontainersConfiguration;
import com.springbootedu.messagingkafka.events.OrderPlaced;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Exercise 1 — a consumer keeps the stock up to date, even when Kafka delivers an event twice.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class Exercise1Test {

    @Autowired
    KafkaTemplate<String, OrderPlaced> kafka;

    @Autowired
    Inventory inventory;

    @Test
    void anOrderLowersTheStock() {
        String isbn = newBookWithStock(10);

        kafka.send(StockUpdater.TOPIC, "o-" + isbn, new OrderPlaced("o-" + isbn, isbn, 3));

        await().atMost(Duration.ofSeconds(20)).until(() -> inventory.available(isbn) == 7);
    }

    @Test
    void theSameEventDeliveredTwiceCountsOnce() {
        String isbn = newBookWithStock(10);
        OrderPlaced order = new OrderPlaced("o-" + isbn, isbn, 4);

        kafka.send(StockUpdater.TOPIC, order.orderId(), order);
        kafka.send(StockUpdater.TOPIC, order.orderId(), order);          // at-least-once: a redelivery
        OrderPlaced marker = new OrderPlaced("marker-" + isbn, isbn, 1);  // sent last, same key → same partition
        kafka.send(StockUpdater.TOPIC, marker.orderId(), marker);

        await().atMost(Duration.ofSeconds(20)).until(() -> inventory.processed(marker.orderId()));
        assertThat(inventory.available(isbn)).isEqualTo(10 - 4 - 1);
    }

    private String newBookWithStock(int quantity) {
        String isbn = UUID.randomUUID().toString().substring(0, 13);
        inventory.receive(isbn, quantity);
        return isbn;
    }
}
