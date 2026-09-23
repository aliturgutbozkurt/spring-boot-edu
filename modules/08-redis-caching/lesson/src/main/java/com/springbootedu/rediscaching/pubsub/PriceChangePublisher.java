package com.springbootedu.rediscaching.pubsub;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * Lesson 3.6 — publishes a message; Redis delivers it to every current subscriber.
 */
// tag::publish[]
@Component
public class PriceChangePublisher {

    public static final String CHANNEL = "price-changes";

    private final StringRedisTemplate redis;
    private final JsonMapper json;

    public PriceChangePublisher(StringRedisTemplate redis, JsonMapper json) {
        this.redis = redis;
        this.json = json;
    }

    public void publish(PriceChange change) {
        redis.convertAndSend(CHANNEL, json.writeValueAsString(change));   // PUBLISH price-changes {...}
    }
}
// end::publish[]
