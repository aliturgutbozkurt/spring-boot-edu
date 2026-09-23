package com.springbootedu.setupmodernjava.gatherers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Lesson 3.8 — stream gatherers.
 */
class SalesStatisticsTest {

    @Test
    void batchesIsbnsForABulkApi() {
        assertThat(SalesStatistics.batches(List.of("a", "b", "c", "d", "e"), 2))
                .containsExactly(List.of("a", "b"), List.of("c", "d"), List.of("e"));
    }

    @Test
    void movingAverageOverThreeDays() {
        assertThat(SalesStatistics.movingAverage(List.of(10, 20, 30, 40), 3)).containsExactly(20.0, 30.0);
    }

    @Test
    void runningTotal() {
        assertThat(SalesStatistics.runningTotal(List.of(5, 10, 20))).containsExactly(5, 15, 35);
    }
}
