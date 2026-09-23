package com.springbootedu.datamongodb.exercise1;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Exercise 1 — product queries.
 */
public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<Product> findBySku(String sku);

    // TODO 1b: replace this default method with a derived query: products of a category, cheapest first
    default List<Product> findByCategoryOrderByPrice(String category) {
        throw new UnsupportedOperationException("TODO 1b");
    }

    // TODO 1c: replace this default method with a JSON query on the "color" entry of the attributes map
    default List<Product> findByColor(String color) {
        throw new UnsupportedOperationException("TODO 1c");
    }
}
