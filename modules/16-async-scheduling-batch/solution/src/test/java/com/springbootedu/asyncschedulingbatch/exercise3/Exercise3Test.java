package com.springbootedu.asyncschedulingbatch.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Exercise 3 — a nightly report job with a date parameter, started by a scheduler.
 */
@SpringBatchTest
@SpringBootTest
class Exercise3Test {

    @TestConfiguration(proxyBeanMethods = false)
    static class FixedClock {

        @Bean
        @Primary                                                     // replaces the system clock in this test
        Clock testClock() {
            return Clock.fixed(Instant.parse("2026-09-24T23:00:00Z"), ZoneId.of("Europe/Istanbul"));  // 02:00 local
        }
    }

    @Autowired
    JobOperatorTestUtils jobs;

    @Autowired
    @Qualifier("dailySalesReportJob")
    Job dailySalesReportJob;

    @Autowired
    ReportArchive archive;

    @Autowired
    NightlyReportScheduler scheduler;

    @BeforeEach
    void setUp() {
        jobs.setJob(dailySalesReportJob);
        archive.clear();
    }

    @Test
    void theJobReportsTheSalesOfTheGivenDay() throws Exception {
        var execution = jobs.startJob(new JobParametersBuilder()
                .addLocalDate("report.date", LocalDate.parse("2026-09-23")).toJobParameters());

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(archive.reportFor(LocalDate.parse("2026-09-23"))).hasValueSatisfying(report -> {
            assertThat(report.orders()).isEqualTo(3);
            assertThat(report.revenue()).isEqualByComparingTo("240.80");
        });
    }

    @Test
    void theSchedulerReportsYesterday() {
        scheduler.createReport();                                        // what @Scheduled calls at 02:00

        assertThat(archive.reportFor(LocalDate.parse("2026-09-24"))).isPresent();   // the clock says 2026-09-25
    }
}
