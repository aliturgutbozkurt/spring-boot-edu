package com.springbootedu.rediscaching.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.rediscaching.TestcontainersConfiguration;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Exercise 3 — a fixed-window rate limiter with INCR and EXPIRE.
 */
@DataRedisTest
@Import({TestcontainersConfiguration.class, RateLimiter.class})
class Exercise3Test {

    private static final Duration MINUTE = Duration.ofMinutes(1);

    @TestConfiguration(proxyBeanMethods = false)
    static class Clocks {

        @Bean
        MutableClock clock() {
            return new MutableClock(Instant.parse("2026-09-23T10:00:00Z"));
        }
    }

    @Autowired
    RateLimiter limiter;

    @Autowired
    MutableClock clock;

    @Autowired
    StringRedisTemplate redis;

    @BeforeEach
    void reset() {
        redis.delete(redis.keys("rate:*"));
    }

    @Test
    void allowsRequestsUpToTheLimitThenRejects() {
        assertThat(limiter.tryAcquire("client-a", 3, MINUTE)).isTrue();
        assertThat(limiter.tryAcquire("client-a", 3, MINUTE)).isTrue();
        assertThat(limiter.tryAcquire("client-a", 3, MINUTE)).isTrue();
        assertThat(limiter.tryAcquire("client-a", 3, MINUTE)).isFalse();
    }

    @Test
    void clientsHaveTheirOwnCounters() {
        limiter.tryAcquire("client-a", 1, MINUTE);

        assertThat(limiter.tryAcquire("client-a", 1, MINUTE)).isFalse();
        assertThat(limiter.tryAcquire("client-b", 1, MINUTE)).isTrue();
    }

    @Test
    void aNewWindowStartsWithAFreshCount() {
        limiter.tryAcquire("client-a", 1, MINUTE);
        assertThat(limiter.tryAcquire("client-a", 1, MINUTE)).isFalse();

        clock.advance(MINUTE);

        assertThat(limiter.tryAcquire("client-a", 1, MINUTE)).isTrue();
    }

    @Test
    void counterKeysExpireWithTheirWindow() {
        limiter.tryAcquire("client-a", 3, MINUTE);

        assertThat(redis.keys("rate:client-a:*")).singleElement()
                .satisfies(key -> assertThat(redis.getExpire(Objects.requireNonNull(key))).isBetween(1L, 60L));
    }
}
