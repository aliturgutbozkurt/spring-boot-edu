package com.springbootedu.asyncschedulingbatch.async;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.asyncschedulingbatch.TestcontainersConfiguration;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Lesson 3.1 — @Async calls run in parallel, here on virtual threads.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class PriceAggregatorTest {

    private static final List<String> ISBNS = List.of("9780134685991", "9781617297571", "9780321336781",
            "9781449373320", "9781492078005", "9780134685991", "9781617297571", "9780321336781",
            "9781449373320", "9781492078005");

    @Autowired
    PriceAggregator aggregator;

    @Autowired
    PriceClient prices;

    @Test
    void tenSlowCallsTakeAboutAsLongAsOne() {
        long start = System.nanoTime();

        BigDecimal total = aggregator.totalFor(ISBNS);

        assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofSeconds(1));   // sequentially: 2 s
        assertThat(total).isEqualByComparingTo("849.80");
    }

    @Test
    void theCallsRunOnVirtualThreads() {
        aggregator.totalFor(ISBNS);

        assertThat(prices.threadsSeen()).isNotEmpty().allMatch(thread -> thread.startsWith("virtual:"));
    }

    @Test
    void aFailingCallFailsTheFuture() {
        assertThat(prices.priceOf("unknown")).failsWithin(Duration.ofSeconds(1))
                .withThrowableThat().withMessageContaining("No price for unknown");
    }
}
