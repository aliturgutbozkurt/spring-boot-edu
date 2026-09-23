package com.springbootedu.datamongodb;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mongodb.MongoDBContainer;

/**
 * Given: one real MongoDB for all tests, started as a single-node replica set.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:8.0.32")   // same image as compose.yaml
            .withReplicaSet();                              // Testcontainers 2: opt-in; needed for transactions

    @Bean
    @ServiceConnection
    MongoDBContainer mongoContainer() {         // not "mongo": Boot already has a MongoClient bean with that name
        return MONGO;
    }
}
