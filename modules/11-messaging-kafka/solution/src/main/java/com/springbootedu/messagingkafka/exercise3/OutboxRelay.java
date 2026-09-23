package com.springbootedu.messagingkafka.exercise3;

import com.springbootedu.messagingkafka.events.OrderPlaced;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

/**
 * Exercise 3 — moves unsent outbox rows to Kafka.
 */
@Component
public class OutboxRelay {

    public static final String TOPIC = "ex3-orders";

    record PendingEvent(long id, String aggregateId, String payload) {
    }

    private final JdbcClient jdbc;
    private final JsonMapper json;
    private final KafkaTemplate<String, OrderPlaced> kafka;

    public OutboxRelay(JdbcClient jdbc, JsonMapper json, KafkaTemplate<String, OrderPlaced> kafka) {
        this.jdbc = jdbc;
        this.json = json;
        this.kafka = kafka;
    }

    @Transactional
    public int relayBatch(int batchSize) {
        List<PendingEvent> pending = jdbc.sql("""
                        SELECT id, aggregate_id, payload FROM outbox
                        WHERE sent_at IS NULL
                        ORDER BY id
                        LIMIT :limit
                        FOR UPDATE SKIP LOCKED""")
                .param("limit", batchSize)
                .query(PendingEvent.class)
                .list();
        for (PendingEvent event : pending) {
            try {
                kafka.send(TOPIC, event.aggregateId(), json.readValue(event.payload(), OrderPlaced.class))
                        .get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new IllegalStateException("Kafka did not confirm outbox event " + event.id(), e);
            }
            jdbc.sql("UPDATE outbox SET sent_at = now() WHERE id = :id").param("id", event.id()).update();
        }
        return pending.size();
    }
}
