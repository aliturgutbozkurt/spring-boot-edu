package com.springbootedu.capstone.search.index;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Creates the "books" index with the mapping of {@link BookDocument} at startup, if it does not exist yet.
 */
interface BookRepository extends ElasticsearchRepository<BookDocument, String> {
}
