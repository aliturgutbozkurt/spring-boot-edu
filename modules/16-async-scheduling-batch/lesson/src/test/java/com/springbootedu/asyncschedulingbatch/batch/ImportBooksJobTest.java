package com.springbootedu.asyncschedulingbatch.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.springbootedu.asyncschedulingbatch.TestcontainersConfiguration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * Lesson 3.4 — a chunk-oriented job: read CSV, validate, write to PostgreSQL; skip bad lines; restart after a crash.
 */
@SpringBatchTest                                        // provides JobOperatorTestUtils and JobRepositoryTestUtils
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class ImportBooksJobTest {

    @Autowired
    JobOperatorTestUtils jobs;

    @Autowired
    JobRepositoryTestUtils repository;

    @Autowired
    JobOperator operator;

    @Autowired
    ImportFaults faults;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    void clean() {
        repository.removeJobExecutions();                  // forget earlier runs: the same parameters can run again
        jdbc.sql("DELETE FROM imported_book").update();
    }

    @AfterEach
    void noMoreCrashes() {
        faults.crashOn(null);
    }

    @Test
    void importsTheValidLinesAndSkipsTheInvalidOnes() throws Exception {
        JobExecution execution = jobs.startJob(parameters("classpath:import/books-2026-09.csv"));

        StepExecution step = execution.getStepExecutions().iterator().next();
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(step.getReadCount()).isEqualTo(7);
        assertThat(step.getWriteCount()).isEqualTo(5);
        assertThat(step.getSkipCount()).isEqualTo(2);                     // broken ISBN, negative price
        assertThat(jdbc.sql("SELECT count(*) FROM imported_book").query(Long.class).single()).isEqualTo(5);
    }

    @Test
    void aCompletedJobInstanceCannotRunAgain() throws Exception {
        JobParameters sameParameters = parameters("classpath:import/books-2026-09.csv");
        jobs.startJob(sameParameters);

        assertThatExceptionOfType(JobInstanceAlreadyCompleteException.class)
                .isThrownBy(() -> jobs.startJob(sameParameters));
    }

    @Test
    void aFailedJobIsRestartedFromTheLastCommittedChunk(@TempDir Path dir) throws Exception {
        Path file = thirtyBooks(dir);
        faults.crashOn(isbn(25));                                          // a crash in the third chunk

        JobExecution first = jobs.startJob(parameters(file.toUri().toString()));

        assertThat(first.getStatus()).isEqualTo(BatchStatus.FAILED);
        assertThat(jdbc.sql("SELECT count(*) FROM imported_book").query(Long.class).single()).isEqualTo(20);

        faults.crashOn(null);                                              // "fix the problem"
        JobExecution second = operator.restart(first);

        assertThat(second.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(second.getStepExecutions().iterator().next().getReadCount()).isEqualTo(10);   // lines 21–30 only
        assertThat(jdbc.sql("SELECT count(*) FROM imported_book").query(Long.class).single()).isEqualTo(30);
    }

    private static JobParameters parameters(String inputFile) {
        return new JobParametersBuilder().addString("input.file", inputFile).toJobParameters();
    }

    private static Path thirtyBooks(Path dir) throws IOException {
        String lines = IntStream.rangeClosed(1, 30)
                .mapToObj(i -> isbn(i) + ",Book " + i + ",10.00")
                .collect(Collectors.joining("\n", "isbn,title,price\n", "\n"));
        return Files.writeString(dir.resolve("thirty-books.csv"), lines);
    }

    private static String isbn(int number) {
        return "97800000%05d".formatted(number);                           // 13 digits
    }
}
