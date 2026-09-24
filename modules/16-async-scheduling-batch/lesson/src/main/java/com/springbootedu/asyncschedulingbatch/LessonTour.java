package com.springbootedu.asyncschedulingbatch;

import com.springbootedu.asyncschedulingbatch.async.PriceAggregator;
import java.util.List;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

/**
 * Runs the lesson's examples once (section 3).
 * Start it with: ./mvnw -pl modules/16-async-scheduling-batch/lesson spring-boot:run   (Docker must be running)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final PriceAggregator prices;
    private final JobOperator jobs;
    private final Job importBooksJob;

    LessonTour(PriceAggregator prices, JobOperator jobs, Job importBooksJob) {
        this.prices = prices;
        this.jobs = jobs;
        this.importBooksJob = importBooksJob;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        section("3.1 @Async on virtual threads");
        long start = System.nanoTime();
        var total = prices.totalFor(List.of("9780134685991", "9781617297571", "9780321336781",
                "9781449373320", "9781492078005"));
        print("5 price calls of 200 ms: total " + total + " in " + (System.nanoTime() - start) / 1_000_000 + " ms");

        section("3.3 A Spring Batch job");
        var parameters = new JobParametersBuilder()
                .addString("input.file", "classpath:import/books-2026-09.csv")
                .addLong("run", System.currentTimeMillis())           // a new job instance on every start
                .toJobParameters();
        JobExecution execution = jobs.start(importBooksJob, parameters);
        var step = execution.getStepExecutions().iterator().next();
        print("status " + execution.getStatus() + ": read " + step.getReadCount() + ", written "
              + step.getWriteCount() + ", skipped " + step.getSkipCount());

        section("3.5 The same parameters again");
        try {
            jobs.start(importBooksJob, parameters);
        } catch (JobInstanceAlreadyCompleteException e) {
            print("refused: " + e.getMessage());
        }

        section("3.2 @Scheduled");
        print("a stock snapshot runs every 30 s; the nightly report runs at 02:00 Europe/Istanbul (Ctrl+C stops the app)");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
