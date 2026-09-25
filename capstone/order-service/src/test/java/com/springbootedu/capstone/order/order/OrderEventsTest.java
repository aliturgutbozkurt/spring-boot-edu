package com.springbootedu.capstone.order.order;

import static com.springbootedu.capstone.order.FakeStockService.EFFECTIVE_JAVA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.springbootedu.capstone.contracts.events.OrderPlaced;
import com.springbootedu.capstone.order.OrderTest;
import com.springbootedu.capstone.order.TopicReader;
import com.springbootedu.capstone.order.Tokens;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

/**
 * C.4 — the outbox relay publishes OrderPlaced to Kafka, and the notification listener consumes it.
 */
@OrderTest
class OrderEventsTest {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    JdbcClient jdbc;

    @Autowired
    KafkaConnectionDetails kafka;                                   // the Testcontainers broker

    String customer = "customer-" + UUID.randomUUID();

    @Test
    void thePlacedOrderIsPublishedWithItsIdAsKey() {
        String id = placeOrder();

        List<String> events = TopicReader.valuesWithKey(kafka, OrderPlaced.TOPIC, id);

        assertThat(events).singleElement().asString()
                .contains("\"orderId\":\"" + id + "\"", "\"isbn\":\"" + EFFECTIVE_JAVA + "\"")
                .doesNotContain("@class");                          // plain JSON: any consumer can read it
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(jdbc.sql("SELECT count(*) FROM outbox WHERE event_key = ? AND published_at IS NOT NULL")
                        .param(id).query(Integer.class).single()).isEqualTo(1));
    }

    @Test
    void theCustomerIsNotifiedOnce() {
        String id = placeOrder();

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(jdbc.sql("SELECT customer_id FROM notifications WHERE order_id = ?::uuid").param(id)
                        .query(String.class).list()).containsExactly(customer));
    }

    private String placeOrder() {
        MvcTestResult result = mvc.post().uri("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, Tokens.bearer(customer, "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"lines\":[{\"isbn\":\"" + EFFECTIVE_JAVA + "\",\"quantity\":1}]}").exchange();
        assertThat(result).hasStatus(HttpStatus.CREATED);
        String location = result.getResponse().getHeader(HttpHeaders.LOCATION);
        return location.substring(location.lastIndexOf('/') + 1);
    }
}
