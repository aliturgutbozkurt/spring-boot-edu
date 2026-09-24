package com.springbootedu.reactive.stream;

import com.springbootedu.reactive.TestcontainersConfiguration;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Lesson 3.6 — Server-Sent Events: the server pushes new orders to every connected client.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "bookstore.tour.enabled=false")
@AutoConfigureWebTestClient
@Import(TestcontainersConfiguration.class)
class OrderStreamTest {

    @Autowired
    WebTestClient http;

    @Test
    void connectedClientsReceiveNewOrdersAsEvents() {
        Flux<OrderEvent> events = http.get().uri("/api/orders/stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .returnResult(OrderEvent.class)
                .getResponseBody();

        StepVerifier.create(events.take(2))
                .then(() -> place("9780134685991", 1))            // placed after the client connected
                .then(() -> place("9781617297571", 2))
                .expectNextMatches(event -> event.isbn().equals("9780134685991"))
                .expectNextMatches(event -> event.quantity() == 2)
                .verifyComplete();
    }

    private void place(String isbn, int quantity) {
        http.mutate().responseTimeout(Duration.ofSeconds(5)).build()
                .post().uri("/api/orders").bodyValue(new OrderEvent(isbn, quantity))
                .exchange().expectStatus().isAccepted();
    }
}
