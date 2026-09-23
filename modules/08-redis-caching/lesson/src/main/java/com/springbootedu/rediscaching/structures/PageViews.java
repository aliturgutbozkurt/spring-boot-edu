package com.springbootedu.rediscaching.structures;

import java.time.Duration;
import java.time.LocalDate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.5 — atomic counters with an expiry: one key per page and day.
 */
// tag::counter[]
@Component
public class PageViews {

    private final StringRedisTemplate redis;

    public PageViews(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public long count(String page) {
        String key = keyForToday(page);
        Long views = redis.opsForValue().increment(key);                  // INCR: safe with many concurrent callers
        redis.expire(key, Duration.ofDays(2));                            // Redis deletes old counters itself
        return views == null ? 0 : views;
    }
    // end::counter[]

    public long today(String page) {
        String value = redis.opsForValue().get(keyForToday(page));
        return value == null ? 0 : Long.parseLong(value);
    }

    public String keyForToday(String page) {
        return "views:" + page + ":" + LocalDate.now();
    }
}
