package com.springbootedu.testing;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Lesson 3.7 — one container for all tests of this JVM; optionally reused across test runs.
 */
// tag::testcontainers[]
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
            DockerImageName.parse("pgvector/pgvector:0.8.6-pg18").asCompatibleSubstituteFor("postgres"))
            .withReuse(true);        // only if ~/.testcontainers.properties says testcontainers.reuse.enable=true

    @Bean
    @ServiceConnection               // Boot derives the DataSource (and Flyway) settings from the container
    PostgreSQLContainer postgresContainer() {
        return POSTGRES;
    }
}
// end::testcontainers[]
