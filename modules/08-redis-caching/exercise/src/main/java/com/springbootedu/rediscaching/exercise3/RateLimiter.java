package com.springbootedu.rediscaching.exercise3;

import java.time.Clock;
import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Exercise 3 — a fixed-window rate limiter: at most {@code limit} calls per client per window.
 */
@Component
public class RateLimiter {

    private final StringRedisTemplate redis;
    private final Clock clock;

    public RateLimiter(StringRedisTemplate redis, Clock clock) {
        this.redis = redis;
        this.clock = clock;
    }

    public boolean tryAcquire(String clientId, int limit, Duration window) {
        // TODO 3a: one key per client and window: "rate:<clientId>:<window number>" (window number = now / window length)
        // TODO 3b: count the call atomically in Redis
        // TODO 3c: the first call of a window makes the key expire after one window
        // TODO 3d: allow the call if the count is within the limit
        throw new UnsupportedOperationException("TODO 3 — " + redis + clock);
    }
}
