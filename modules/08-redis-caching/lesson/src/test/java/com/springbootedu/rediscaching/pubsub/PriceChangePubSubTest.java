package com.springbootedu.rediscaching.pubsub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.springbootedu.rediscaching.TestcontainersConfiguration;
import java.math.BigDecimal;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Lesson 3.6 — publish/subscribe: fire-and-forget messages to whoever is listening right now.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class PriceChangePubSubTest {

    @Autowired
    PriceChangePublisher publisher;

    @Autowired
    PriceChangeListener listener;

    @Test
    void theListenerReceivesPublishedMessages() {
        publisher.publish(new PriceChange("9780134685991", new BigDecimal("79.90")));

        await().atMost(Duration.ofSeconds(5))                          // delivery is asynchronous
                .untilAsserted(() -> assertThat(listener.received())
                        .contains(new PriceChange("9780134685991", new BigDecimal("79.90"))));
    }
}
