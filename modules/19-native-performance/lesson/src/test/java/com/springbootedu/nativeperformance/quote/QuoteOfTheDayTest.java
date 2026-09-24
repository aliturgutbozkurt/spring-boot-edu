package com.springbootedu.nativeperformance.quote;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

/**
 * Lesson 3.4 — a classpath resource: in a native image it exists only if a hint includes it.
 */
class QuoteOfTheDayTest {

    @Test
    void theQuoteDependsOnTheDay() {
        var firstOfJanuary = new QuoteOfTheDay(Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneOffset.UTC));
        var secondOfJanuary = new QuoteOfTheDay(Clock.fixed(Instant.parse("2026-01-02T10:00:00Z"), ZoneOffset.UTC));

        assertThat(firstOfJanuary.today()).isNotBlank().isNotEqualTo(secondOfJanuary.today());
        assertThat(firstOfJanuary.count()).isEqualTo(5);
    }
}
