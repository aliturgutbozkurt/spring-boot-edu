package com.springbootedu.modulith;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * One real PostgreSQL (event publication registry) and one real Kafka (externalized events) for all tests.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    static final KafkaContainer KAFKA = new KafkaContainer("apache/kafka:4.2.1");

    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
            DockerImageName.parse("pgvector/pgvector:0.8.6-pg18").asCompatibleSubstituteFor("postgres"));

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        return KAFKA;
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return POSTGRES;
    }

    /**
     * On shutdown, Modulith's publication registry reads the unfinished publications to log them. The shared
     * container is stopped by the first test context that closes, so the others would wait 30 s for a connection.
     */
    @Bean
    DynamicPropertyRegistrar failFastWhenTheDatabaseIsGone() {
        return registry -> registry.add("spring.datasource.hikari.connection-timeout", () -> "500");   // milliseconds
    }
}
