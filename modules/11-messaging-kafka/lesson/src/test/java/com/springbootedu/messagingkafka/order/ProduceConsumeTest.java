package com.springbootedu.messagingkafka.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.springbootedu.messagingkafka.TestcontainersConfiguration;
import com.springbootedu.messagingkafka.invoicing.Invoices;
import com.springbootedu.messagingkafka.shipping.Shipments;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.support.SendResult;

/**
 * Lessons 3.1–3.2 — a producer sends JSON events; every consumer group receives every event once.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class ProduceConsumeTest {

    @Autowired
    OrderEvents events;

    @Autowired
    Shipments shipments;

    @Autowired
    Invoices invoices;

    @Test
    void theBrokerConfirmsTheSendWithPartitionAndOffset() throws Exception {
        SendResult<String, OrderPlaced> result = events.publish(order()).get();

        assertThat(result.getRecordMetadata().topic()).isEqualTo(OrderEvents.TOPIC);
        assertThat(result.getRecordMetadata().offset()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void everyConsumerGroupReceivesTheEvent() {
        OrderPlaced order = order();
        events.publish(order);

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            assertThat(shipments.shipped()).contains(order);             // group "shipping"
            assertThat(invoices.invoiced()).contains(order.orderId());  // group "invoicing"
        });
    }

    private static OrderPlaced order() {
        return new OrderPlaced(UUID.randomUUID().toString(), "customer-1", "9780134685991", 1);
    }
}
