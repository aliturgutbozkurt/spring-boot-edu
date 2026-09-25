package com.springbootedu.capstone.order;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * One PostgreSQL per JVM for all order tests.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    static final KafkaContainer KAFKA = new KafkaContainer("apache/kafka:4.2.1");

    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18-alpine");

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgres() {
        return POSTGRES;
    }

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        return KAFKA;
    }
}
