package com.springbootedu.reactive;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Given: one PostgreSQL (R2DBC + Flyway) and one MongoDB for all tests (same images as compose.yaml).
 */
// tag::testcontainers[]
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
            DockerImageName.parse("pgvector/pgvector:0.8.6-pg18").asCompatibleSubstituteFor("postgres"));

    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:8.0.32");

    @Bean
    @ServiceConnection                    // provides R2DBC *and* JDBC (Flyway) connection details
    PostgreSQLContainer postgresContainer() {
        return POSTGRES;
    }

    @Bean
    @ServiceConnection
    MongoDBContainer mongoContainer() {
        return MONGO;
    }
}
// end::testcontainers[]
