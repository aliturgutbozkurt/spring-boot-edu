package com.springbootedu.capstone.order.outbox;

import java.time.Clock;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ADR-3 — publishes the outbox rows to Kafka, oldest first, and marks them. The payload is already JSON,
 * so it is sent as a plain string. A crash after sending but before marking sends the row again:
 * at-least-once delivery, so every consumer must be idempotent.
 */
@Component
class OutboxRelay {

    private final OutboxRepository events;
    private final KafkaTemplate<String, String> kafka;
    private final Clock clock;

    OutboxRelay(OutboxRepository events, KafkaTemplate<String, String> kafka, Clock clock) {
        this.events = events;
        this.kafka = kafka;
        this.clock = clock;
    }

    // tag::relay[]
    @Scheduled(fixedDelayString = "${bookstore.outbox.relay-interval}")
    @Transactional
    public void publishPending() throws ExecutionException, InterruptedException, TimeoutException {
        List<OutboxEvent> pending = events.lockNextToPublish();          // other instances skip these rows
        for (OutboxEvent event : pending) {
            kafka.send(event.getTopic(), event.getEventKey(), event.getPayload())
                    .get(10, TimeUnit.SECONDS);                          // wait for the broker's acknowledgement
            event.markPublished(clock.instant());                       // saved at commit (dirty checking)
        }
    }
}
    // end::relay[]
