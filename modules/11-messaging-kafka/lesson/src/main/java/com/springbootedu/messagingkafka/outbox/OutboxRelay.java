package com.springbootedu.messagingkafka.outbox;

import com.springbootedu.messagingkafka.order.OrderEvents;
import com.springbootedu.messagingkafka.order.OrderPlaced;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

/**
 * Lesson 3.6 — sends the unsent outbox rows to Kafka and marks them as sent.
 */
// tag::outbox-relay[]
@Component
public class OutboxRelay {

    record PendingEvent(long id, String payload) {
    }

    private final JdbcClient jdbc;
    private final JsonMapper json;
    private final OrderEvents events;

    public OutboxRelay(JdbcClient jdbc, JsonMapper json, OrderEvents events) {
        this.jdbc = jdbc;
        this.json = json;
        this.events = events;
    }

    @Scheduled(fixedDelayString = "${bookstore.outbox.relay-interval}")
    @Transactional
    public int relay() throws ExecutionException, InterruptedException, TimeoutException {
        List<PendingEvent> pending = jdbc.sql("""
                        SELECT id, payload FROM outbox
                        WHERE sent_at IS NULL
                        ORDER BY id
                        LIMIT 100
                        FOR UPDATE SKIP LOCKED""")                     // several relays never send the same row
                .query(PendingEvent.class)
                .list();
        for (PendingEvent event : pending) {
            events.publish(json.readValue(event.payload(), OrderPlaced.class))
                    .get(10, TimeUnit.SECONDS);                         // wait for the broker's acknowledgement
            jdbc.sql("UPDATE outbox SET sent_at = now() WHERE id = :id").param("id", event.id()).update();
        }
        return pending.size();
    }
}
// end::outbox-relay[]
