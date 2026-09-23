package com.springbootedu.datajdbcpostgres;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Given: one real PostgreSQL for all tests, started by Testcontainers (see lesson section 3.7).
 * {@code @ServiceConnection} hands its URL, user and password to Spring Boot — no spring.datasource.* needed.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    // same image as compose.yaml; tell Testcontainers it behaves like the official "postgres" image
    static final DockerImageName IMAGE = DockerImageName.parse("pgvector/pgvector:0.8.6-pg18")
            .asCompatibleSubstituteFor("postgres");

    // static: one container per JVM, shared by every cached Spring test context
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(IMAGE);

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgres() {
        return POSTGRES;
    }
}
