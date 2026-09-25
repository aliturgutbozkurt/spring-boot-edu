package com.springbootedu.capstone.order.outbox;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * ADR-3 — an event waiting to be published to Kafka. It is written in the same transaction as the data
 * it describes, so the two can never get out of sync.
 */
@Entity
@Table(name = "outbox")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Nullable Long id;

    private String topic;

    private String eventKey;

    private String payload;                                     // the event as JSON

    private Instant createdAt;

    private @Nullable Instant publishedAt;                      // null until the relay has sent it

    protected OutboxEvent() {                                   // for JPA
    }

    public OutboxEvent(String topic, String eventKey, String payload, Instant createdAt) {
        this.topic = topic;
        this.eventKey = eventKey;
        this.payload = payload;
        this.createdAt = createdAt;
    }

    public @Nullable Long getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getEventKey() {
        return eventKey;
    }

    public String getPayload() {
        return payload;
    }

    public @Nullable Instant getPublishedAt() {
        return publishedAt;
    }

    public void markPublished(Instant when) {
        this.publishedAt = when;
    }
}
