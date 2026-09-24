package com.springbootedu.asyncschedulingbatch.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Exercise 1 — ask three carriers at the same time and take the cheapest answer.
 */
@SpringBootTest
class Exercise1Test {

    @Autowired
    ShippingQuotes quotes;

    @Test
    void findsTheCheapestCarrier() {
        Quote cheapest = quotes.cheapest("9780134685991");

        assertThat(cheapest.carrier()).isEqualTo("yurtici");
        assertThat(cheapest.price()).isEqualByComparingTo("24.90");
    }

    @Test
    void asksAllCarriersInParallel() {
        long start = System.nanoTime();

        quotes.cheapest("9780134685991");

        assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofMillis(700));   // one call: 300 ms
    }

    @Test
    void aFailingCarrierIsIgnored() {
        Quote cheapest = quotes.cheapest("9781617297571");            // "yurtici" cannot deliver this one

        assertThat(cheapest.carrier()).isEqualTo("mng");
        assertThat(cheapest.price()).isEqualByComparingTo(new BigDecimal("29.90"));
    }
}
