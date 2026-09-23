package com.springbootedu.elasticsearch.exercise3;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.index.AliasAction;
import org.springframework.data.elasticsearch.core.index.AliasActionParameters;
import org.springframework.data.elasticsearch.core.index.AliasActions;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

/**
 * Exercise 3 — builds a new index next to the old one, then switches the alias in one atomic step.
 */
@Service
public class Reindexer {

    public static final String ALIAS = "catalog";

    private static final AtomicLong RUNS = new AtomicLong();

    private final ElasticsearchOperations operations;
    private final BookSource source;

    public Reindexer(ElasticsearchOperations operations, BookSource source) {
        this.operations = operations;
        this.source = source;
    }

    public String reindex() {
        String newIndex = ALIAS + "-" + System.currentTimeMillis() + "-" + RUNS.incrementAndGet();
        IndexOperations fresh = operations.indexOps(IndexCoordinates.of(newIndex));
        fresh.create(fresh.createSettings(CatalogBook.class), fresh.createMapping(CatalogBook.class));

        operations.save(source.allBooks(), IndexCoordinates.of(newIndex));
        fresh.refresh();

        IndexOperations alias = operations.indexOps(IndexCoordinates.of(ALIAS));
        Set<String> oldIndices = alias.exists() ? alias.getAliases(ALIAS).keySet() : Set.of();

        AliasActions actions = new AliasActions(new AliasAction.Add(
                AliasActionParameters.builder().withIndices(newIndex).withAliases(ALIAS).build()));
        if (!oldIndices.isEmpty()) {
            actions.add(new AliasAction.Remove(AliasActionParameters.builder()
                    .withIndices(oldIndices.toArray(String[]::new)).withAliases(ALIAS).build()));
        }
        fresh.alias(actions);                                           // add + remove: one atomic switch

        oldIndices.forEach(old -> operations.indexOps(IndexCoordinates.of(old)).delete());
        return newIndex;
    }
}
