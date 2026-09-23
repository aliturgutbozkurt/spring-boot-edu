package com.springbootedu.rediscaching.pubsub;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * Lesson 3.6 — receives messages of the channel it is subscribed to (see PubSubConfiguration).
 */
@Component
public class PriceChangeListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(PriceChangeListener.class);

    private final JsonMapper json;
    private final List<PriceChange> received = new CopyOnWriteArrayList<>();

    public PriceChangeListener(JsonMapper json) {
        this.json = json;
    }

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        PriceChange change = json.readValue(new String(message.getBody(), StandardCharsets.UTF_8), PriceChange.class);
        received.add(change);
        log.info("Price changed: {} → {}", change.isbn(), change.newPrice());
    }

    public List<PriceChange> received() {
        return List.copyOf(received);
    }
}
