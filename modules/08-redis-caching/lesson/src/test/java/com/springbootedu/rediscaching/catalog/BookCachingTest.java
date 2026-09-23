package com.springbootedu.rediscaching.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.rediscaching.TestcontainersConfiguration;
import java.math.BigDecimal;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Lessons 3.1–3.3 — the Spring cache abstraction backed by Redis.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class BookCachingTest {

    private static final String ISBN = "9780134685991";

    @Autowired
    BookService books;

    @Autowired
    SlowBookRepository repository;

    @Autowired
    CacheManager caches;

    @Autowired
    StringRedisTemplate redis;

    @BeforeEach
    void reset() {
        Objects.requireNonNull(caches.getCache("books")).invalidate();    // clear() may be deferred; invalidate() is immediate
        repository.reset();
    }

    @Test
    void theSecondCallIsServedFromTheCache() {
        books.find(ISBN);
        books.find(ISBN);

        assertThat(repository.calls()).isEqualTo(1);
    }

    @Test
    void cachePutRefreshesTheCachedValue() {
        books.find(ISBN);
        books.changePrice(ISBN, new BigDecimal("79.90"));

        assertThat(books.find(ISBN).price()).isEqualByComparingTo("79.90");
        assertThat(repository.calls()).isEqualTo(2);    // first find + the read inside changePrice; the last find hit the cache
    }

    @Test
    void cacheEvictForcesTheNextReadToTheSource() {
        books.find(ISBN);
        books.remove(ISBN);

        assertThat(books.findOrNull(ISBN)).isNull();
        assertThat(repository.calls()).isEqualTo(2);
    }

    @Test
    void entriesAreJsonInRedisWithATimeToLive() {
        books.find(ISBN);

        assertThat(redis.opsForValue().get("books::" + ISBN)).contains("\"title\":\"Effective Java\"");
        assertThat(redis.getExpire("books::" + ISBN)).isBetween(1L, 600L);            // TTL of 10 minutes
    }

    @Test
    void missingBooksAreNotCached() {
        assertThat(books.findOrNull("9780000000000")).isNull();
        assertThat(redis.hasKey("books::9780000000000")).isFalse();
    }
}
