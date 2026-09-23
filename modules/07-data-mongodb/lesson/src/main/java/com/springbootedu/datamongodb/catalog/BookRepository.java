package com.springbootedu.datamongodb.catalog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Lesson 3.2 — the same repository programming model as JPA, translated into MongoDB queries.
 */
// tag::repository[]
public interface BookRepository extends MongoRepository<Book, String> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAuthorsContaining(String author);                 // matches one element of the array

    List<Book> findByPriceLessThanOrderByPrice(BigDecimal max);

    @Query("{ 'attributes.language': ?0 }")                             // a MongoDB JSON query
    List<Book> findByLanguage(String language);
}
// end::repository[]
