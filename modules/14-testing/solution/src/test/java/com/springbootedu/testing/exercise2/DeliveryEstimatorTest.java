package com.springbootedu.testing.exercise2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

/**
 * Exercise 2 — the formerly flaky tests, now deterministic.
 */
class DeliveryEstimatorTest {

    private static Clock fixedAt(String isoDate) {
        return Clock.fixed(Instant.parse(isoDate + "T10:00:00Z"), ZoneOffset.UTC);   // the same "today" on every run
    }

    @Test
    void anOrderOnMondayArrivesOnThursday() {
        var estimator = new DeliveryEstimator(fixedAt("2026-09-21"));                   // a Monday

        assertThat(estimator.deliveryDate()).isEqualTo(LocalDate.parse("2026-09-24"));
    }

    @Test
    void theWeekendDoesNotCount() {
        var estimator = new DeliveryEstimator(fixedAt("2026-09-24"));                   // a Thursday

        assertThat(estimator.deliveryDate()).isEqualTo(LocalDate.parse("2026-09-29"));  // Fri, Mon, Tue
    }

    @Test
    void theMarmaraRegionIsServedByFourWarehouses() {
        assertThat(new Warehouses().serving("marmara"))
                .containsExactlyInAnyOrder("istanbul-asia", "istanbul-europe", "bursa", "kocaeli");   // a Set has no order
    }

    @Test
    void aShipmentIsDispatchedEventually() {
        var tracker = new ShipmentTracker();

        tracker.dispatch();

        await().atMost(Duration.ofSeconds(2)).until(() -> tracker.status().equals("DISPATCHED"));   // no fixed sleep
    }
}
