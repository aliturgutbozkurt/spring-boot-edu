package com.springbootedu.testing.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * Exercise 2 — these tests are flaky: they pass on some days, runs or machines and fail on others.
 * Make each of them deterministic without changing the production code.
 */
class DeliveryEstimatorTest {

    @Test
    void anOrderArrivesInThreeDays() {
        // TODO 2a: depends on today's date — it fails when a weekend falls into the next three days
        var estimator = new DeliveryEstimator(Clock.systemDefaultZone());

        assertThat(estimator.deliveryDate()).isEqualTo(LocalDate.now().plusDays(3));
    }

    @Test
    void theMarmaraRegionIsServedByFourWarehouses() {
        // TODO 2b: depends on the iteration order of a HashSet, which is not guaranteed
        assertThat(new Warehouses().serving("marmara"))
                .containsExactly("istanbul-asia", "istanbul-europe", "bursa", "kocaeli");
    }

    @Test
    void aShipmentIsDispatchedEventually() throws InterruptedException {
        // TODO 2c: depends on timing — the dispatch takes 50–400 ms, the test waits a fixed 200 ms
        var tracker = new ShipmentTracker();

        tracker.dispatch();
        Thread.sleep(200);

        assertThat(tracker.status()).isEqualTo("DISPATCHED");
    }
}
