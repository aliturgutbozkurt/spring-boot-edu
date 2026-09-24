package com.springbootedu.graphqlwebsocket.exercise3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureGraphQlTester
class Exercise3Test {

    private static final String PLACE_ORDER = """
            mutation($isbn: ID!) {
              placeOrder(input: { customerId: "7", lines: [{ isbn: $isbn, quantity: 1 }] }) { id }
            }""";

    @LocalServerPort
    int port;

    @Autowired
    GraphQlTester graphQl;

    StompSession session;

    @BeforeEach
    void connect() throws Exception {
        var client = new WebSocketStompClient(new StandardWebSocketClient());
        client.setMessageConverter(new JacksonJsonMessageConverter());
        session = client.connectAsync("ws://localhost:" + port + "/ws", new StompSessionHandlerAdapter() {
        }).get(5, TimeUnit.SECONDS);
    }

    @AfterEach
    void disconnect() {
        session.disconnect();
    }

    @Test
    void everyStockChangeIsPushedToTheBooksTopic() {
        BlockingQueue<StockLevel> levels = subscribe("/topic/stock/9780321336781", StockLevel.class);

        // the SUBSCRIBE frame travels asynchronously: order again until the first message arrives
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            order("9780321336781");
            StockLevel level = levels.poll(300, TimeUnit.MILLISECONDS);
            assertThat(level).isNotNull();
            assertThat(level.isbn()).isEqualTo("9780321336781");
            assertThat(level.remaining()).isLessThan(50);
        });
    }

    @Test
    void onlyALowStockTriggersAnAlert() {
        BlockingQueue<StockAlert> alerts = subscribe("/topic/stock-alerts", StockAlert.class);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            order("9780134685991");                      // 10 copies: no alert yet
            order("9781449373320");                      // 3 copies → fewer than 3 left: alert
            StockAlert alert = alerts.poll(300, TimeUnit.MILLISECONDS);
            assertThat(alert).isNotNull();
            assertThat(alert.isbn()).isEqualTo("9781449373320");
            assertThat(alert.remaining()).isLessThan(3);
        });
        assertThat(List.copyOf(alerts)).extracting(StockAlert::isbn).doesNotContain("9780134685991");
    }

    private void order(String isbn) {
        graphQl.document(PLACE_ORDER).variables(Map.of("isbn", isbn)).execute();
    }

    private <T> BlockingQueue<T> subscribe(String destination, Class<T> type) {
        BlockingQueue<T> received = new LinkedBlockingQueue<>();
        session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                received.add(type.cast(payload));
            }
        });
        return received;
    }
}
