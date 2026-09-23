package com.springbootedu.datamongodb.exercise1;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Exercise 1 — product queries.
 */
public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<Product> findBySku(String sku);

    List<Product> findByCategoryOrderByPrice(String category);

    @Query("{ 'attributes.color': ?0 }")
    List<Product> findByColor(String color);
}
