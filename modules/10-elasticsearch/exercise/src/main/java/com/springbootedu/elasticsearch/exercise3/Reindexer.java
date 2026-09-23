package com.springbootedu.elasticsearch.exercise3;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

/**
 * Exercise 3 — builds a new index next to the old one, then switches the alias in one atomic step.
 */
@Service
public class Reindexer {

    public static final String ALIAS = "catalog";

    private final ElasticsearchOperations operations;
    private final BookSource source;

    public Reindexer(ElasticsearchOperations operations, BookSource source) {
        this.operations = operations;
        this.source = source;
    }

    public String reindex() {
        // TODO 3a: create a new index "catalog-<unique suffix>" with the settings and mapping of CatalogBook
        // TODO 3b: write every book of the source into it and refresh it
        // TODO 3c: find the indices the alias points to now (there may be none on the first run)
        // TODO 3d: in ONE alias request, add the alias to the new index and remove it from the old ones
        // TODO 3e: delete the old indices and return the name of the new one
        throw new UnsupportedOperationException("TODO 3 — " + operations + source);
    }
}
