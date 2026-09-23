package com.springbootedu.elasticsearch.exercise2;

import org.jspecify.annotations.Nullable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

/**
 * Exercise 2 — search with facets that stay useful after a category was chosen.
 */
@Service
public class FacetedSearch {

    private final ElasticsearchOperations operations;

    public FacetedSearch(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    public SearchPage search(String text, @Nullable String category, @Nullable Double maxPrice) {
        // TODO 2a: products whose name matches the text; with maxPrice, only products up to that price
        // TODO 2b: a terms aggregation "categories" on the category field
        // TODO 2c: the chosen category must filter the hits, but NOT the category counts
        // TODO 2d: return the hits and the counts per category
        throw new UnsupportedOperationException("TODO 2 — " + text + category + maxPrice + operations);
    }
}
