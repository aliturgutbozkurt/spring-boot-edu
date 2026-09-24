package com.springbootedu.asyncschedulingbatch.scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.CronExpression;

/**
 * Lesson 3.2 — a cron expression is tested by asking for the next run, not by waiting for it.
 */
class NightlyReportCronTest {

    private final CronExpression nightly = CronExpression.parse("0 0 2 * * *");     // bookstore.report.cron

    @Test
    void runsAtTwoInTheMorning() {
        ZonedDateTime evening = ZonedDateTime.parse("2026-09-24T22:15:00+03:00[Europe/Istanbul]");

        assertThat(nightly.next(evening)).isEqualTo(ZonedDateTime.parse("2026-09-25T02:00:00+03:00[Europe/Istanbul]"));
    }

    @Test
    void runsOnceADay() {
        ZonedDateTime first = ZonedDateTime.parse("2026-09-25T02:00:00+03:00[Europe/Istanbul]");

        assertThat(nightly.next(first)).isEqualTo(first.plusDays(1));
    }
}
