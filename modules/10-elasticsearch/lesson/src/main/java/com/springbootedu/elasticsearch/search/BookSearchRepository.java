package com.springbootedu.elasticsearch.search;

import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Lesson 3.2 — a Spring Data repository: CRUD and derived queries, as with JPA or MongoDB.
 */
// tag::repository[]
public interface BookSearchRepository extends ElasticsearchRepository<BookDocument, String> {

    List<BookDocument> findByAuthor(String author);                   // term query on the keyword field
}
// end::repository[]
