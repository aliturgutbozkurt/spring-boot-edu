package com.springbootedu.capstone.order.outbox;

import java.time.Clock;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

/**
 * ADR-3 — stores an event in the outbox. It must run inside the caller's transaction (MANDATORY):
 * an outbox row without the order it describes would be a lie.
 */
@Component
public class Outbox {

    private final OutboxRepository events;
    private final JsonMapper json;
    private final Clock clock;

    Outbox(OutboxRepository events, JsonMapper json, Clock clock) {
        this.events = events;
        this.json = json;
        this.clock = clock;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void add(String topic, String key, Object event) {
        events.save(new OutboxEvent(topic, key, json.writeValueAsString(event), clock.instant()));
    }
}
