package com.springbootedu.setupmodernjava.virtualthreads;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;

/**
 * Lesson 3.5 — virtual threads make blocking code scale.
 */
class PriceLookupTest {

    @Test
    void tenThousandBlockingCallsFinishQuicklyOnVirtualThreads() {
        var result = PriceLookup.lookupAll(10_000, Duration.ofMillis(100));

        assertThat(result.completed()).isEqualTo(10_000);
        assertThat(result.elapsed()).isLessThan(Duration.ofSeconds(5));   // sequentially: ~17 minutes
    }

    @Test
    void everyTaskRunsOnAVirtualThread() {
        assertThat(PriceLookup.runsOnVirtualThread()).isTrue();
    }
}
