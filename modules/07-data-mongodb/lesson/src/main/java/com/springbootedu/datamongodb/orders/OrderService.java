package com.springbootedu.datamongodb.orders;

import java.time.Instant;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lesson 3.6 — two collections, one transaction.
 */
// tag::transactional[]
@Service
public class OrderService {

    private final MongoTemplate mongo;

    public OrderService(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Transactional                                          // uses the MongoTransactionManager bean
    public void place(String customer, String isbn, int quantity) {
        mongo.insert(new Order(null, customer, isbn, quantity, Instant.now()));   // 1st document …

        Inventory inventory = mongo.findById(isbn, Inventory.class);
        if (inventory == null || inventory.quantity() < quantity) {
            throw new IllegalStateException("Not enough stock for " + isbn);      // … is rolled back here
        }
        mongo.save(new Inventory(isbn, inventory.quantity() - quantity));        // 2nd document
    }
}
// end::transactional[]
