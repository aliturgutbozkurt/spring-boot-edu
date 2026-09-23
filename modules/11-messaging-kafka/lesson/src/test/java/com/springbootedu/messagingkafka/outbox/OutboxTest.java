package com.springbootedu.messagingkafka.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import com.springbootedu.messagingkafka.TestcontainersConfiguration;
import com.springbootedu.messagingkafka.order.OrderPlaced;
import com.springbootedu.messagingkafka.shipping.Shipments;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * Lesson 3.6 — the order and its event are saved in ONE database transaction; a relay sends the event later.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class OutboxTest {

    @Autowired
    OrderService orders;

    @Autowired
    Shipments shipments;

    @Autowired
    JdbcClient jdbc;

    @Test
    void aPlacedOrderIsRelayedToKafka() {
        OrderPlaced placed = orders.place("customer-7", "9781617297571", 2);

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            assertThat(shipments.shipped()).contains(placed);
            assertThat(sentAt(placed.orderId())).isNotNull();               // the relay marked it as sent
        });
    }

    @Test
    void aFailedOrderLeavesNeitherAnOrderNorAnEvent() {
        // the order row and the outbox row are written first, then the stock check fails → everything rolls back
        assertThatThrownBy(() -> orders.place("customer-rollback", "9781617297571", 999))
                .isInstanceOf(OutOfStockException.class);

        assertThat(count("SELECT count(*) FROM orders WHERE customer_id = 'customer-rollback'")).isZero();
        assertThat(count("SELECT count(*) FROM outbox WHERE payload LIKE '%customer-rollback%'")).isZero();
    }

    private long count(String sql) {
        return jdbc.sql(sql).query(Long.class).single();
    }

    private Object sentAt(String orderId) {
        return jdbc.sql("SELECT sent_at FROM outbox WHERE aggregate_id = :id")
                .param("id", orderId).query((rs, n) -> rs.getTimestamp(1)).optional().orElse(null);
    }
}
