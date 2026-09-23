package com.springbootedu.messagingkafka.exercise3;

import com.springbootedu.messagingkafka.events.OrderPlaced;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * Exercise 3 — moves unsent outbox rows to Kafka.
 */
@Component
public class OutboxRelay {

    public static final String TOPIC = "ex3-orders";

    private final JdbcClient jdbc;
    private final JsonMapper json;
    private final KafkaTemplate<String, OrderPlaced> kafka;

    public OutboxRelay(JdbcClient jdbc, JsonMapper json, KafkaTemplate<String, OrderPlaced> kafka) {
        this.jdbc = jdbc;
        this.json = json;
        this.kafka = kafka;
    }

    public int relayBatch(int batchSize) {
        // TODO 3a: in one transaction, read at most batchSize unsent rows, oldest first,
        //          and lock them so that a second relay skips them
        // TODO 3b: send each payload (as OrderPlaced) to TOPIC with aggregate_id as key, and wait for the broker
        // TODO 3c: mark each sent row with sent_at = now()
        // TODO 3d: return how many rows were sent
        throw new UnsupportedOperationException("TODO 3 — " + batchSize + jdbc + json + kafka);
    }
}
