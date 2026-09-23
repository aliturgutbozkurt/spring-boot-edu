package com.springbootedu.rediscaching.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.rediscaching.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Exercise 2 — a game leaderboard on a Redis sorted set.
 */
@DataRedisTest
@Import({TestcontainersConfiguration.class, Leaderboard.class})
class Exercise2Test {

    @Autowired
    Leaderboard leaderboard;

    @Autowired
    StringRedisTemplate redis;

    @BeforeEach
    void reset() {
        redis.delete("leaderboard");
    }

    @Test
    void pointsAccumulatePerPlayer() {
        leaderboard.addPoints("ada", 10);
        leaderboard.addPoints("ada", 15);

        assertThat(leaderboard.top(1)).containsExactly(new PlayerScore("ada", 25));
    }

    @Test
    void theTopListIsHighestFirstAndLimited() {
        leaderboard.addPoints("ada", 40);
        leaderboard.addPoints("linus", 70);
        leaderboard.addPoints("grace", 55);
        leaderboard.addPoints("alan", 10);

        assertThat(leaderboard.top(3)).containsExactly(
                new PlayerScore("linus", 70),
                new PlayerScore("grace", 55),
                new PlayerScore("ada", 40));
    }

    @Test
    void ranksStartAtOneAndUnknownPlayersHaveNone() {
        leaderboard.addPoints("ada", 40);
        leaderboard.addPoints("linus", 70);

        assertThat(leaderboard.rankOf("linus")).hasValue(1L);
        assertThat(leaderboard.rankOf("ada")).hasValue(2L);
        assertThat(leaderboard.rankOf("nobody")).isEmpty();
    }
}
