package com.springbootedu.rediscaching.exercise1;

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
 * Exercise 1 — a product detail cache that is invalidated on every update.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestcontainersConfiguration.class)
class Exercise1Test {

    @Autowired
    ProductDetailService details;

    @Autowired
    ProductCatalog catalog;

    @Autowired
    CacheManager caches;

    @Autowired
    StringRedisTemplate redis;

    @BeforeEach
    void reset() {
        Objects.requireNonNull(caches.getCache("product-details")).invalidate();
        catalog.reset();
    }

    @Test
    void theSecondReadIsServedFromTheCache() {
        details.detail("p-1");
        details.detail("p-1");

        assertThat(catalog.reads()).isEqualTo(1);
    }

    @Test
    void anUpdateInvalidatesTheCachedDetail() {
        details.detail("p-1");
        details.changePrice("p-1", new BigDecimal("39.90"));

        assertThat(details.detail("p-1").price()).isEqualByComparingTo("39.90");
        assertThat(catalog.reads()).isEqualTo(2);
    }

    @Test
    void anUpdateLeavesOtherProductsCached() {
        details.detail("p-1");
        details.detail("p-2");
        details.changePrice("p-1", new BigDecimal("39.90"));
        details.detail("p-2");

        assertThat(catalog.reads()).isEqualTo(2);
    }

    @Test
    void entriesAreJsonWithAFiveMinuteTimeToLive() {
        details.detail("p-1");

        assertThat(redis.opsForValue().get("product-details::p-1")).contains("\"name\":\"Notebook\"");
        assertThat(redis.getExpire("product-details::p-1")).isBetween(1L, 300L);
    }
}
