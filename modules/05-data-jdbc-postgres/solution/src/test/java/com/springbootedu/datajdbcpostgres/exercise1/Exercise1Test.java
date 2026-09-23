package com.springbootedu.datajdbcpostgres.exercise1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.datajdbcpostgres.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * Exercise 1 — a Flyway migration for the review table.
 */
@JdbcTest
@Import(TestcontainersConfiguration.class)
class Exercise1Test {

    @Autowired
    JdbcClient jdbc;

    private int insertReview(long bookId, int stars) {
        return jdbc.sql("insert into review (book_id, stars, comment) values (?, ?, 'test')")
                .params(bookId, stars).update();
    }

    @Test
    void theThirdMigrationWasApplied() {
        assertThat(jdbc.sql("select version from flyway_schema_history where success order by installed_rank")
                .query(String.class).list()).containsExactly("1", "2", "3");
    }

    @Test
    void acceptsAValidReviewAndFillsCreatedAt() {
        assertThat(insertReview(1, 5)).isEqualTo(1);
        assertThat(jdbc.sql("select created_at from review where comment = 'test'")
                .query(java.time.OffsetDateTime.class).single()).isNotNull();
    }

    // One failing statement per test: after an error PostgreSQL rejects everything else in the same transaction.
    @Test
    void zeroStarsAreRejected() {
        assertThatThrownBy(() -> insertReview(1, 0)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void sixStarsAreRejected() {
        assertThatThrownBy(() -> insertReview(1, 6)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void aReviewMustBelongToAnExistingBook() {
        assertThatThrownBy(() -> insertReview(999, 4)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deletingABookDeletesItsReviews() {
        insertReview(2, 4);
        jdbc.sql("delete from book where id = 2").update();

        assertThat(jdbc.sql("select count(*) from review where book_id = 2").query(Integer.class).single()).isZero();
    }
}
