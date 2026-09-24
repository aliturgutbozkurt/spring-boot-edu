package com.springbootedu.asyncschedulingbatch.batch;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Lessons 3.3–3.5 — reader → processor → writer, in chunks of 10, with skip, retry and restart.
 */
@Configuration(proxyBeanMethods = false)
class ImportBooksJobConfiguration {

    // tag::reader[]
    @Bean
    @StepScope                                                   // a new reader per step run, with the job's parameters
    FlatFileItemReader<BookLine> bookLineReader(@Value("#{jobParameters['input.file']}") Resource input) {
        return new FlatFileItemReaderBuilder<BookLine>()
                .name("bookLineReader")                          // the key of its position in the ExecutionContext
                .resource(input)
                .linesToSkip(1)                                  // the header line
                .delimited().names("isbn", "title", "price")
                .targetType(BookLine.class)                      // records are supported
                .build();
    }
    // end::reader[]

    // tag::writer[]
    @Bean
    JdbcBatchItemWriter<ImportedBook> importedBookWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<ImportedBook>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO imported_book (isbn, title, price) VALUES (:isbn, :title, :price)
                        ON CONFLICT (isbn) DO UPDATE SET title = EXCLUDED.title, price = EXCLUDED.price,
                                                         imported_at = now()""")     // idempotent: safe to rerun
                .itemSqlParameterSourceProvider(book -> new MapSqlParameterSource()
                        .addValue("isbn", book.isbn()).addValue("title", book.title()).addValue("price", book.price()))
                .build();
    }
    // end::writer[]

    // tag::step[]
    @Bean
    Step importStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                    FlatFileItemReader<BookLine> reader, BookLineProcessor processor,
                    JdbcBatchItemWriter<ImportedBook> writer, ImportSkipLog skipLog) {
        return new StepBuilder("importStep", jobRepository)
                .<BookLine, ImportedBook>chunk(10)                           // 10 items per transaction
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(InvalidBookLineException.class)                       // bad data: skip the item …
                .skip(FlatFileParseException.class)
                .skipLimit(10)                                              // … but not more than 10 times
                .retry(TransientDataAccessException.class)                  // a short database hiccup: try again
                .retryLimit(3)
                .skipListener(skipLog)
                .build();
    }

    @Bean
    Job importBooksJob(JobRepository jobRepository, Step importStep) {
        return new JobBuilder("importBooksJob", jobRepository)
                .start(importStep)
                .build();
    }
    // end::step[]
}
