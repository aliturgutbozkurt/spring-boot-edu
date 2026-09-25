package com.springbootedu.capstone.search.query;

import com.springbootedu.capstone.search.index.BookDocument;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

/**
 * Full-text search over title, authors and description, with typo tolerance. Results are cached in Redis
 * per normalized query; {@link com.springbootedu.capstone.search.index.BookIndex} evicts them.
 */
@Service
public class BookSearch {

    private final ElasticsearchOperations elasticsearch;

    BookSearch(ElasticsearchOperations elasticsearch) {
        this.elasticsearch = elasticsearch;
    }

    // tag::search[]
    @Cacheable(cacheNames = "search", key = "#query.strip().toLowerCase()")    // "Java" and " java" share an entry
    public SearchResult search(String query) {
        NativeQuery search = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(match -> match
                        .query(query)
                        .fields("title^3", "authors^2", "description")      // a title match counts most
                        .fuzziness("AUTO")))                                // "efective" still finds "effective"
                .withMaxResults(20)
                .build();
        var hits = elasticsearch.search(search, BookDocument.class).stream()
                .map(SearchHit::getContent).map(BookHit::from).toList();
        return new SearchResult(query, hits);
    }
}
    // end::search[]
