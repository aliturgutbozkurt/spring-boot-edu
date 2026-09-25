package com.springbootedu.capstone.order.notification;

import com.springbootedu.capstone.contracts.events.OrderPlaced;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Architecture 3.1, step 5 — confirms a placed order to the customer. Here the "message" is stored and logged;
 * a real system would send an e-mail. ON CONFLICT DO NOTHING makes a redelivered event harmless.
 */
@Component
class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    private final JdbcClient jdbc;
    private final Clock clock;

    NotificationListener(JdbcClient jdbc, Clock clock) {
        this.jdbc = jdbc;
        this.clock = clock;
    }

    @KafkaListener(topics = OrderPlaced.TOPIC, groupId = "order-notifications",
            properties = "spring.json.value.default.type=com.springbootedu.capstone.contracts.events.OrderPlaced")
    void onOrderPlaced(OrderPlaced order) {
        String message = "Thank you! Your order %s over %s is confirmed.".formatted(order.orderId(), order.total());
        int inserted = jdbc.sql("""
                        INSERT INTO notifications (order_id, customer_id, message, created_at)
                        VALUES (:orderId, :customerId, :message, :createdAt)
                        ON CONFLICT (order_id) DO NOTHING""")
                .param("orderId", UUID.fromString(order.orderId()))
                .param("customerId", order.customerId())
                .param("message", message)
                .param("createdAt", clock.instant().atOffset(ZoneOffset.UTC))
                .update();
        if (inserted == 1) {
            log.info("notified {}: {}", order.customerId(), message);
        }
    }
}
