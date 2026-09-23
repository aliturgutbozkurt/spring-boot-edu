package com.springbootedu.rediscaching.exercise3;

import java.time.Clock;
import java.time.Duration;
import java.util.Objects;
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
        long windowNumber = clock.millis() / window.toMillis();
        String key = "rate:" + clientId + ":" + windowNumber;                  // one counter per client and window
        long count = Objects.requireNonNull(redis.opsForValue().increment(key)); // INCR is atomic
        if (count == 1) {
            redis.expire(key, window);                                          // the first call starts the countdown
        }
        return count <= limit;
    }
}
