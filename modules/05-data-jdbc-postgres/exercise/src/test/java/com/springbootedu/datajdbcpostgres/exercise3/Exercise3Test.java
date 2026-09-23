package com.springbootedu.datajdbcpostgres.exercise3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.datajdbcpostgres.TestcontainersConfiguration;
import com.springbootedu.datajdbcpostgres.exercise2.NewReview;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * Exercise 3 — an all-or-nothing import whose outcome is always logged.
 * Not a @JdbcTest: the transactions must really commit or roll back, so the test cleans up itself.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class Exercise3Test {

    @Autowired
    ReviewImportService imports;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    @AfterEach                                        // this test commits for real: leave the shared database clean
    void clean() {
        jdbc.sql("delete from review").update();
        jdbc.sql("delete from import_log").update();
    }

    private int reviewCount() {
        return jdbc.sql("select count(*) from review").query(Integer.class).single();
    }

    private List<String> logStatuses() {
        return jdbc.sql("select status from import_log order by id").query(String.class).list();
    }

    @Test
    void aValidImportSavesEverythingAndLogsSuccess() {
        int imported = imports.importAll("9780134685991", List.of(
                new NewReview("9780134685991", 5, "a"), new NewReview("9780134685991", 4, "b")));

        assertThat(imported).isEqualTo(2);
        assertThat(reviewCount()).isEqualTo(2);
        assertThat(logStatuses()).containsExactly("SUCCESS");
    }

    @Test
    void oneInvalidReviewRollsBackTheWholeImportButTheFailureIsLogged() {
        assertThatThrownBy(() -> imports.importAll("9780134685991", List.of(
                new NewReview("9780134685991", 5, "ok"), new NewReview("9780134685991", 9, "invalid stars"))))
                .isInstanceOf(RuntimeException.class);

        assertThat(reviewCount()).isZero();                  // the valid first review was rolled back too
        assertThat(logStatuses()).containsExactly("FAILED");
    }
}
