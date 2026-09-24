package com.springbootedu.observability.pricing;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Lesson 3.4 — one annotation, a timer with the observation's name and tags.
 */
@SpringBootTest
class ObservedTest {

    @Autowired
    PricingService pricing;

    @Autowired
    MeterRegistry registry;

    @Test
    void anObservedMethodIsTimed() {
        pricing.priceOf("9780134685991");

        var timer = registry.get("bookstore.pricing").tag("currency", "TRY").timer();
        assertThat(timer.count()).isPositive();
        assertThat(timer.totalTime(TimeUnit.MILLISECONDS)).isGreaterThanOrEqualTo(50);   // the method sleeps 50 ms
    }
}
