package com.springbootedu.asyncschedulingbatch.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.2 — runs at fixed times, like cron. The time zone is explicit: servers often run in UTC.
 */
// tag::cron[]
@Component
class NightlyReportTask {

    private static final Logger log = LoggerFactory.getLogger(NightlyReportTask.class);

    @Scheduled(cron = "${bookstore.report.cron}", zone = "Europe/Istanbul")
    void createReport() {
        log.info("nightly report created");                             // with several instances: see ShedLock (3.2)
    }
}
// end::cron[]
