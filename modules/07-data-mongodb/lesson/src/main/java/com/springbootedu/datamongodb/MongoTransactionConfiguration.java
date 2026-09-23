package com.springbootedu.datamongodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

/**
 * Lesson 3.6 — Spring Boot does not register a MongoDB transaction manager on its own: @Transactional
 * needs this bean. Transactions require a replica set (compose.yaml and Testcontainers both start one).
 */
// tag::transaction-manager[]
@Configuration(proxyBeanMethods = false)
public class MongoTransactionConfiguration {

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory databaseFactory) {
        return new MongoTransactionManager(databaseFactory);
    }
}
// end::transaction-manager[]
