package com.springbootedu.modulith;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Given: one PostgreSQL for all tests.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
            DockerImageName.parse("pgvector/pgvector:0.8.6-pg18").asCompatibleSubstituteFor("postgres"));

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return POSTGRES;
    }

    /** The shared container stops with the first test context; the others must not wait 30 s on shutdown. */
    @Bean
    DynamicPropertyRegistrar failFastWhenTheDatabaseIsGone() {
        return registry -> registry.add("spring.datasource.hikari.connection-timeout", () -> "500");   // milliseconds
    }
}
