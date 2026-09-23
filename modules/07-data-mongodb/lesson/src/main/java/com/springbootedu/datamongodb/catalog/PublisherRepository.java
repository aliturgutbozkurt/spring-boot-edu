package com.springbootedu.datamongodb.catalog;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Lesson 3.1 — publishers live in their own collection.
 */
public interface PublisherRepository extends MongoRepository<Publisher, String> {
}
