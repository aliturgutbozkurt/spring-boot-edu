package com.springbootedu.rediscaching.structures;

import java.util.List;
import java.util.Objects;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.4 — a sorted set: members ordered by score, updated atomically.
 */
// tag::sorted-set[]
@Component
public class BestSellers {

    private static final String KEY = "bestsellers";

    public record Entry(String title, long sold) {
    }

    private final StringRedisTemplate redis;

    public BestSellers(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void recordSale(String title, int quantity) {
        redis.opsForZSet().incrementScore(KEY, title, quantity);          // ZINCRBY
    }

    public List<Entry> top(int count) {
        var ranked = redis.opsForZSet().reverseRangeWithScores(KEY, 0, count - 1);   // ZREVRANGE … WITHSCORES
        return Objects.requireNonNull(ranked).stream()
                .map(tuple -> new Entry(Objects.requireNonNull(tuple.getValue()),
                        Math.round(Objects.requireNonNull(tuple.getScore()))))
                .toList();
    }
}
// end::sorted-set[]
