package com.springbootedu.rediscaching.exercise2;

import java.util.List;
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
        // TODO 2a: add the points to the player's score in the sorted set KEY
        throw new UnsupportedOperationException("TODO 2a — " + redis);
    }

    public List<PlayerScore> top(int count) {
        // TODO 2b: the best `count` players, highest score first
        throw new UnsupportedOperationException("TODO 2b");
    }

    public Optional<Long> rankOf(String player) {
        // TODO 2c: 1 for the leader, 2 for the runner-up …; empty for players without points
        throw new UnsupportedOperationException("TODO 2c");
    }
}
