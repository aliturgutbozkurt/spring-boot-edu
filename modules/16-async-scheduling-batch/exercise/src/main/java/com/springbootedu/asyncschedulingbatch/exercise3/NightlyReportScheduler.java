package com.springbootedu.asyncschedulingbatch.exercise3;

import java.time.Clock;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
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

    // TODO 3b: run every night at 02:00 Istanbul time
    public void createReport() {
        // TODO 3c: start dailySalesReportJob with the job parameter "report.date" = yesterday, according to the clock
        throw new UnsupportedOperationException("TODO 3 — " + jobs + dailySalesReportJob + clock);
    }
}
