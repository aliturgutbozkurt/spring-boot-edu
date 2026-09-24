package com.springbootedu.reactive.compare;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;

/**
 * Lesson 3.7 — 200 slow calls (200 ms each): the reactive and the virtual-thread version both finish in about
 * the time of ONE call, because neither blocks a scarce platform thread while waiting.
 */
class ConcurrencyComparisonTest {

    private final ConcurrencyComparison comparison = new ConcurrencyComparison(Duration.ofMillis(200));

    @Test
    void reactiveCallsOverlap() {
        var result = comparison.reactive(200);

        assertThat(result.results()).isEqualTo(200);
        assertThat(result.elapsed()).isLessThan(Duration.ofSeconds(3));   // sequentially: 40 s
    }

    @Test
    void virtualThreadCallsOverlapToo() {
        var result = comparison.virtualThreads(200);

        assertThat(result.results()).isEqualTo(200);
        assertThat(result.elapsed()).isLessThan(Duration.ofSeconds(3));
    }
}
