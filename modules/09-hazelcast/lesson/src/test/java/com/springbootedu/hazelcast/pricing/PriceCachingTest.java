package com.springbootedu.hazelcast.pricing;

import static org.assertj.core.api.Assertions.assertThat;

import com.hazelcast.core.HazelcastInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Lesson 3.4 — the same @Cacheable as in module 08, now stored in a Hazelcast IMap.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
class PriceCachingTest {

    @Autowired
    PriceService prices;

    @Autowired
    HazelcastInstance hazelcast;

    @BeforeEach
    void reset() {
        hazelcast.getMap("prices").clear();
        prices.resetCalls();
    }

    @Test
    void theSecondCallIsServedFromTheCache() {
        prices.priceOf("9780134685991");
        prices.priceOf("9780134685991");

        assertThat(prices.calls()).isEqualTo(1);
    }

    @Test
    void theCacheIsAnIMapOfTheSameName() {
        prices.priceOf("9780134685991");

        assertThat(hazelcast.getMap("prices").containsKey("9780134685991")).isTrue();
    }
}
