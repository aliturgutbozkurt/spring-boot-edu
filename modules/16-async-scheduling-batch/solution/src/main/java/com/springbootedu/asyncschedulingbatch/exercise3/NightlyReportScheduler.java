package com.springbootedu.asyncschedulingbatch.exercise3;

import java.time.Clock;
import java.time.LocalDate;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Exercise 3 — every night at 02:00, the report of the day before.
 */
@Component
public class NightlyReportScheduler {

    private final JobOperator jobs;
    private final Job dailySalesReportJob;
    private final Clock clock;

    public NightlyReportScheduler(JobOperator jobs, @Qualifier("dailySalesReportJob") Job dailySalesReportJob,
                                  Clock clock) {
        this.jobs = jobs;
        this.dailySalesReportJob = dailySalesReportJob;
        this.clock = clock;
    }

    @Scheduled(cron = "0 0 2 * * *", zone = "Europe/Istanbul")
    public void createReport() {
        LocalDate yesterday = LocalDate.now(clock).minusDays(1);
        try {
            jobs.start(dailySalesReportJob, new JobParametersBuilder()
                    .addLocalDate("report.date", yesterday).toJobParameters());
        } catch (Exception e) {
            throw new IllegalStateException("Nightly report for " + yesterday + " failed", e);
        }
    }
}
