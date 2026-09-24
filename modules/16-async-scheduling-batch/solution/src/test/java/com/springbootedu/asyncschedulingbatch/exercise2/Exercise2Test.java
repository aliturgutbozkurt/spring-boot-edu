package com.springbootedu.asyncschedulingbatch.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Exercise 2 — an order import that skips bad lines, but gives up when too many are bad.
 */
@SpringBatchTest
@SpringBootTest
class Exercise2Test {

    @Autowired
    JobOperatorTestUtils jobs;

    @Autowired
    @Qualifier("importOrdersJob")
    Job importOrdersJob;

    @Autowired
    OrderInbox inbox;

    @BeforeEach
    void setUp() {
        jobs.setJob(importOrdersJob);                                  // there is more than one job
        inbox.clear();
    }

    @Test
    void importsTheGoodLinesAndSkipsTheBadOnes() throws Exception {
        JobExecution execution = jobs.startJob(parameters("classpath:import/orders.csv"));

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(inbox.orders()).extracting(ImportedOrder::orderId).containsExactly("o-1", "o-2", "o-4", "o-6", "o-7");
        assertThat(execution.getStepExecutions().iterator().next().getSkipCount()).isEqualTo(2);
    }

    @Test
    void tooManyBadLinesFailTheJob() throws Exception {
        JobExecution execution = jobs.startJob(parameters("classpath:import/orders-mostly-broken.csv"));

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.FAILED);               // 4 bad lines > limit 3
    }

    private static JobParameters parameters(String file) {
        return new JobParametersBuilder().addString("input.file", file)
                .addLong("run", System.nanoTime()).toJobParameters();
    }
}
