package com.springbootedu.asyncschedulingbatch.exercise2;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exercise 2 — the import job: chunks of 5, bad lines skipped, at most 3 of them.
 */
@Configuration(proxyBeanMethods = false)
public class ImportOrdersJobConfiguration {

    @Bean
    Step importOrdersStep(JobRepository jobRepository, FlatFileItemReader<OrderLine> orderLineReader,
                          OrderLineProcessor processor, OrderInbox inbox) {
        return new StepBuilder("importOrdersStep", jobRepository)
                .<OrderLine, ImportedOrder>chunk(5)
                .reader(orderLineReader)
                .processor(processor)
                .writer(inbox)
                .faultTolerant()
                .skip(InvalidOrderLineException.class)
                .skipLimit(3)
                .build();
    }

    @Bean
    Job importOrdersJob(JobRepository jobRepository, Step importOrdersStep) {
        return new JobBuilder("importOrdersJob", jobRepository).start(importOrdersStep).build();
    }
}
