package com.springbootedu.rediscaching.structures;

import java.util.List;
import java.util.Objects;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.4 — a capped list per user: push to the front, trim the tail.
 */
// tag::list[]
@Component
public class RecentlyViewed {

    private final StringRedisTemplate redis;

    public RecentlyViewed(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void view(String user, String isbn) {
        String key = "viewed:" + user;
        redis.opsForList().leftPush(key, isbn);                           // LPUSH
        redis.opsForList().trim(key, 0, 4);                               // LTRIM: keep the newest 5
    }

    public List<String> of(String user) {
        return Objects.requireNonNull(redis.opsForList().range("viewed:" + user, 0, -1));
    }
}
// end::list[]
