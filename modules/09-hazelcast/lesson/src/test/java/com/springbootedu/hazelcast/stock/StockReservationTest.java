package com.springbootedu.hazelcast.stock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Lesson 3.5 — many callers reserve the same stock at once: with a key lock or an entry processor,
 * nothing is sold twice.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
class StockReservationTest {

    private static final String ISBN = "9780134685991";

    @Autowired
    StockService stock;

    @BeforeEach
    void reset() {
        stock.setAvailable(ISBN, 10);
    }

    @Test
    void aReservationLowersTheStockAndFailsWhenNotEnoughIsLeft() {
        assertThat(stock.reserveWithLock(ISBN, 7)).isTrue();
        assertThat(stock.reserveWithLock(ISBN, 7)).isFalse();

        assertThat(stock.available(ISBN)).isEqualTo(3);
    }

    @Test
    void theKeyLockPreventsOverselling() throws InterruptedException {
        int sold = reserveConcurrently(() -> stock.reserveWithLock(ISBN, 1));

        assertThat(sold).isEqualTo(10);
        assertThat(stock.available(ISBN)).isZero();
    }

    @Test
    void theEntryProcessorPreventsOversellingWithoutALock() throws InterruptedException {
        int sold = reserveConcurrently(() -> stock.reserveWithEntryProcessor(ISBN, 1));

        assertThat(sold).isEqualTo(10);
        assertThat(stock.available(ISBN)).isZero();
    }

    @Test
    void tryLockGivesUpWhenTheKeyIsBusy() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var holder = executor.submit(() -> {
                stock.holdLockFor(ISBN, 1_000);                     // another caller holds the lock for 1 s
                return null;
            });
            Thread.sleep(200);

            assertThat(stock.tryReserve(ISBN, 1)).isFalse();
            holder.get();
        }
        assertThat(stock.tryReserve(ISBN, 1)).isTrue();
    }

    private static int reserveConcurrently(BooleanSupplier reservation) throws InterruptedException {
        var sold = new AtomicInteger();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 50; i++) {                   // 50 buyers, 10 books
                executor.submit(() -> {
                    if (reservation.getAsBoolean()) {
                        sold.incrementAndGet();
                    }
                });
            }
        }
        return sold.get();
    }
}
