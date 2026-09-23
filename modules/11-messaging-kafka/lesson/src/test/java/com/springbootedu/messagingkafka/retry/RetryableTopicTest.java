package com.springbootedu.messagingkafka.retry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.springbootedu.messagingkafka.TestcontainersConfiguration;
import com.springbootedu.messagingkafka.invoicing.Invoices;
import com.springbootedu.messagingkafka.order.OrderEvents;
import com.springbootedu.messagingkafka.order.OrderPlaced;
import java.time.Duration;
import java.util.UUID;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Lesson 3.4 — non-blocking retries: a failing event waits in a retry topic while the next events flow on.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class RetryableTopicTest {

    @Autowired
    OrderEvents events;

    @Autowired
    Invoices invoices;

    @Autowired
    KafkaAdmin admin;

    @Test
    void aTemporaryFailureSucceedsOnALaterAttempt() {
        OrderPlaced order = new OrderPlaced(UUID.randomUUID().toString(), "flaky-customer", "9780134685991", 1);
        events.publish(order);

        await().atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> assertThat(invoices.invoiced()).contains(order.orderId()));
        assertThat(invoices.attempts(order.orderId())).isEqualTo(3);        // failed twice, then succeeded
    }

    @Test
    void theRetryAndDeadLetterTopicsAreCreatedAutomatically() throws Exception {
        events.publish(new OrderPlaced(UUID.randomUUID().toString(), "customer-1", "9780134685991", 1)).get();

        try (AdminClient client = AdminClient.create(admin.getConfigurationProperties())) {
            await().atMost(Duration.ofSeconds(20)).untilAsserted(() ->
                    assertThat(client.listTopics().names().get())
                            .contains("orders-invoicing-retry-500", "orders-invoicing-retry-1000", "orders-invoicing-dlt"));
        }
    }
}
