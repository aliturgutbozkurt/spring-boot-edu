package com.springbootedu.capstone.catalog;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.mongodb.MongoDBContainer;

/**
 * MongoDB and a Hazelcast member for all tests (same images as compose.yaml).
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:8.0.32");

    static final GenericContainer<?> HAZELCAST = new GenericContainer<>("hazelcast/hazelcast:5.5.0")
            .withEnv("HZ_CLUSTERNAME", "bookstore")
            .withExposedPorts(5701);

    @Bean
    @ServiceConnection
    MongoDBContainer mongoContainer() {
        return MONGO;
    }

    @Bean
    @ServiceConnection(name = "hazelcast/hazelcast")
    GenericContainer<?> hazelcastMember() {
        return HAZELCAST;
    }
}
