package com.springbootedu.capstone.search.index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.springbootedu.capstone.contracts.events.BookChanged;
import com.springbootedu.capstone.contracts.events.OrderPlaced;
import com.springbootedu.capstone.search.TestcontainersConfiguration;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.json.JsonMapper;

/**
 * C.4 — the search service follows the events of the catalog and of the order service.
 * The test sends plain JSON, exactly as the other services publish it.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class IndexListenersTest {

    @Autowired
    KafkaTemplate<String, String> kafka;

    @Autowired
    JsonMapper json;

    @Autowired
    MockMvcTester mvc;

    String isbn = String.valueOf(ThreadLocalRandom.current().nextLong(1_000_000_000_000L, 9_999_999_999_999L));
    String word = "omega" + UUID.randomUUID().toString().substring(0, 8);

    @Test
    void aBookChangedInTheCatalogBecomesSearchable() {
        publish(BookChanged.TOPIC, isbn, new BookChanged(isbn, "Streams " + word, List.of("Gwen"), "Kafka.",
                new BigDecimal("50.00"), 3));

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(search(word)).contains("\"isbn\":\"" + isbn + "\""));
    }

    @Test
    void aPlacedOrderCountsAsSales() {
        publish(BookChanged.TOPIC, isbn, new BookChanged(isbn, "Sales " + word, List.of("Gwen"), "Kafka.",
                new BigDecimal("50.00"), 3));
        String orderId = UUID.randomUUID().toString();
        var order = new OrderPlaced(orderId, "ayse", List.of(new OrderPlaced.Line(isbn, "Sales", 2,
                new BigDecimal("50.00"))), new BigDecimal("100.00"), Instant.now());

        publish(OrderPlaced.TOPIC, orderId, order);

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(search(word)).contains("\"sold\":2"));
    }

    private void publish(String topic, String key, Object event) {
        kafka.send(topic, key, json.writeValueAsString(event)).join();
    }

    private String search(String q) throws Exception {
        return mvc.get().uri("/api/search").param("q", q).exchange().getResponse().getContentAsString();
    }
}
