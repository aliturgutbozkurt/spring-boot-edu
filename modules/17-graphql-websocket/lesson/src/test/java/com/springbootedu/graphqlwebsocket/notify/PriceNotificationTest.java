package com.springbootedu.graphqlwebsocket.notify;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.springbootedu.graphqlwebsocket.TestcontainersConfiguration;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * Lesson 3.6 — STOMP over WebSocket: every connected client learns about a price change at once.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "bookstore.tour.enabled=false")
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
class PriceNotificationTest {

    @LocalServerPort
    int port;

    @Autowired
    RestTestClient http;

    @Test
    void aPriceChangeIsPushedToSubscribers() throws Exception {
        var client = new WebSocketStompClient(new StandardWebSocketClient());
        client.setMessageConverter(new JacksonJsonMessageConverter());
        StompSession session = client.connectAsync("ws://localhost:" + port + "/ws", new StompSessionHandlerAdapter() {
        }).get(5, TimeUnit.SECONDS);

        BlockingQueue<PriceChanged> received = new LinkedBlockingQueue<>();
        session.subscribe("/topic/prices", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return PriceChanged.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                received.add((PriceChanged) payload);
            }
        });
        // the SUBSCRIBE frame travels asynchronously: repeat the (idempotent) change until it arrives
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            http.put().uri("/api/books/9780321336781/price").contentType(MediaType.APPLICATION_JSON)
                    .body("{\"price\": 49.90}").exchange().expectStatus().isNoContent();
            PriceChanged change = received.poll(300, TimeUnit.MILLISECONDS);
            assertThat(change).isNotNull();
            assertThat(change.isbn()).isEqualTo("9780321336781");
            assertThat(change.newPrice()).isEqualByComparingTo(new BigDecimal("49.90"));
        });
        session.disconnect();
    }
}
