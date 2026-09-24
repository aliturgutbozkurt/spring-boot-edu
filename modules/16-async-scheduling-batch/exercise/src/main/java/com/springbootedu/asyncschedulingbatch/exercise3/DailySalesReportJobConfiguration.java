package com.springbootedu.asyncschedulingbatch.exercise3;

import java.time.LocalDate;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exercise 3 — a one-step job: a tasklet computes the report of the day given as job parameter.
 */
@Configuration(proxyBeanMethods = false)
public class DailySalesReportJobConfiguration {

    @Bean
    @StepScope
    Tasklet dailySalesTasklet(@Value("#{jobParameters['report.date']}") LocalDate date,
                              SalesRecords sales, ReportArchive archive) {
        return (contribution, chunkContext) -> {
            // TODO 3a: count the sales of `date`, add up their amounts and store a DailyReport in the archive
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    Step dailySalesStep(JobRepository jobRepository, Tasklet dailySalesTasklet) {
        return new StepBuilder("dailySalesStep", jobRepository).tasklet(dailySalesTasklet).build();
    }

    @Bean
    Job dailySalesReportJob(JobRepository jobRepository, Step dailySalesStep) {
        return new JobBuilder("dailySalesReportJob", jobRepository).start(dailySalesStep).build();
    }
}
