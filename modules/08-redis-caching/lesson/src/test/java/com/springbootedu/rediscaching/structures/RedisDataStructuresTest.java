package com.springbootedu.rediscaching.structures;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.rediscaching.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Lessons 3.4–3.5 — Redis is more than a cache: sorted sets, lists and atomic counters.
 */
@DataRedisTest
@Import({TestcontainersConfiguration.class, BestSellers.class, RecentlyViewed.class, PageViews.class})
class RedisDataStructuresTest {

    @Autowired
    BestSellers bestSellers;

    @Autowired
    RecentlyViewed recentlyViewed;

    @Autowired
    PageViews pageViews;

    @Autowired
    StringRedisTemplate redis;

    @BeforeEach
    void flush() {
        redis.execute((org.springframework.data.redis.core.RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();                    // tests share one Redis: start empty
            return null;
        });
    }

    @Test
    void sortedSetKeepsTheBestSellersInOrder() {
        bestSellers.recordSale("Effective Java", 3);
        bestSellers.recordSale("Java Puzzlers", 1);
        bestSellers.recordSale("Spring in Action", 5);
        bestSellers.recordSale("Effective Java", 4);                  // 3 + 4 = 7

        assertThat(bestSellers.top(2)).containsExactly(
                new BestSellers.Entry("Effective Java", 7),
                new BestSellers.Entry("Spring in Action", 5));
    }

    @Test
    void listKeepsOnlyTheLastFiveViews() {
        for (int i = 1; i <= 7; i++) {
            recentlyViewed.view("ayse", "isbn-" + i);
        }

        assertThat(recentlyViewed.of("ayse")).containsExactly("isbn-7", "isbn-6", "isbn-5", "isbn-4", "isbn-3");
    }

    @Test
    void countersAreAtomicAndExpire() {
        pageViews.count("home");
        pageViews.count("home");

        assertThat(pageViews.today("home")).isEqualTo(2);
        assertThat(redis.getExpire(pageViews.keyForToday("home"))).isPositive();   // cleaned up by Redis later
    }
}
