package com.springbootedu.rediscaching.exercise2;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Exercise 2 — a leaderboard kept in the sorted set "leaderboard".
 */
@Component
public class Leaderboard {

    private static final String KEY = "leaderboard";

    private final StringRedisTemplate redis;

    public Leaderboard(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void addPoints(String player, long points) {
        redis.opsForZSet().incrementScore(KEY, player, points);                        // ZINCRBY
    }

    public List<PlayerScore> top(int count) {
        var ranked = redis.opsForZSet().reverseRangeWithScores(KEY, 0, count - 1);    // ZREVRANGE … WITHSCORES
        return Objects.requireNonNull(ranked).stream()
                .map(tuple -> new PlayerScore(Objects.requireNonNull(tuple.getValue()),
                        Math.round(Objects.requireNonNull(tuple.getScore()))))
                .toList();
    }

    public Optional<Long> rankOf(String player) {
        return Optional.ofNullable(redis.opsForZSet().reverseRank(KEY, player))       // ZREVRANK: 0-based, null if absent
                .map(rank -> rank + 1);
    }
}
