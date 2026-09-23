package com.springbootedu.datajdbcpostgres;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * Lesson 3.2 — Flyway ran both migrations before the test started.
 */
@JdbcTest
@Import(TestcontainersConfiguration.class)
class FlywayMigrationTest {

    @Autowired
    JdbcClient jdbc;

    @Test
    void appliedTheSchemaAndTheSeedData() {
        assertThat(jdbc.sql("select version from flyway_schema_history where success order by installed_rank")
                .query(String.class).list())
                .containsExactly("1", "2");
        assertThat(jdbc.sql("select count(*) from book").query(Integer.class).single()).isEqualTo(5);
    }

    @Test
    void theDatabaseEnforcesTheRules() {
        assertThat(jdbc.sql("select count(*) from information_schema.check_constraints where constraint_name like 'book_%'")
                .query(Integer.class).single()).isGreaterThanOrEqualTo(2);    // price >= 0, stock >= 0
    }
}
