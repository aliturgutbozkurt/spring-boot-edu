package com.springbootedu.hazelcast.exercise2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import com.hazelcast.core.HazelcastInstance;
import com.springbootedu.hazelcast.TestMembers;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Exercise 2 — reserve every item of an order, or none of them, while many orders run at once.
 */
class Exercise2Test {

    private static final String JAVA = "9780134685991";
    private static final String SPRING = "9781617297571";

    static HazelcastInstance member;

    OrderReservation reservation;

    @BeforeAll
    static void start() {
        member = TestMembers.start("exercise2");
    }

    @AfterAll
    static void stop() {
        member.shutdown();
    }

    @BeforeEach
    void seed() {
        reservation = new OrderReservation(member);
        reservation.setAvailable(JAVA, 10);
        reservation.setAvailable(SPRING, 10);
    }

    @Test
    void reservesEveryItemOfTheOrder() {
        assertThat(reservation.reserveAll(Map.of(JAVA, 2, SPRING, 3))).isTrue();

        assertThat(reservation.available(JAVA)).isEqualTo(8);
        assertThat(reservation.available(SPRING)).isEqualTo(7);
    }

    @Test
    void reservesNothingWhenOneItemIsShort() {
        assertThat(reservation.reserveAll(Map.of(JAVA, 2, SPRING, 11))).isFalse();

        assertThat(reservation.available(JAVA)).isEqualTo(10);
        assertThat(reservation.available(SPRING)).isEqualTo(10);
    }

    @Test
    void concurrentOrdersNeverOversell() throws InterruptedException {
        var confirmed = new AtomicInteger();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 40; i++) {                            // 40 orders, stock for 10
                executor.submit(() -> {
                    if (reservation.reserveAll(Map.of(JAVA, 1, SPRING, 1))) {
                        confirmed.incrementAndGet();
                    }
                });
            }
        }

        assertThat(confirmed).hasValue(10);
        assertThat(reservation.available(JAVA)).isZero();
        assertThat(reservation.available(SPRING)).isZero();
    }

    @Test
    void ordersThatNameTheSameBooksInOppositeOrderDoNotDeadlock() {
        reservation.setAvailable(JAVA, 1_000);
        reservation.setAvailable(SPRING, 1_000);

        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                for (int i = 0; i < 200; i++) {
                    executor.submit(() -> reservation.reserveAll(orderOf(JAVA, SPRING)));
                    executor.submit(() -> reservation.reserveAll(orderOf(SPRING, JAVA)));
                }
            }
        });
        assertThat(reservation.available(JAVA)).isEqualTo(600);
    }

    /**
     * An order whose map iterates its books in exactly the given order.
     */
    private static Map<String, Integer> orderOf(String first, String second) {
        var order = new LinkedHashMap<String, Integer>();
        order.put(first, 1);
        order.put(second, 1);
        return order;
    }
}
