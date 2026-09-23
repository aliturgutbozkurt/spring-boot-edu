package com.springbootedu.elasticsearch.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.elasticsearch.TestcontainersConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.elasticsearch.test.autoconfigure.DataElasticsearchTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;

/**
 * Exercise 3 — rebuild the whole index without a moment in which searches find nothing.
 */
@DataElasticsearchTest
@Import({TestcontainersConfiguration.class, Reindexer.class, Exercise3Test.Catalog.class})
class Exercise3Test {

    /**
     * A catalog whose books the test can change.
     */
    static class Catalog implements BookSource {

        final List<CatalogBook> books = new ArrayList<>();

        @Override
        public List<CatalogBook> allBooks() {
            return List.copyOf(books);
        }
    }

    @Autowired
    Reindexer reindexer;

    @Autowired
    Catalog catalog;

    @Autowired
    ElasticsearchOperations operations;

    @BeforeEach
    void reset() {
        var alias = operations.indexOps(IndexCoordinates.of(Reindexer.ALIAS));
        if (alias.exists()) {                                           // wildcard deletes are not allowed
            aliasTargets().forEach(index -> operations.indexOps(IndexCoordinates.of(index)).delete());
        }
        catalog.books.clear();
        catalog.books.add(new CatalogBook("1", "Effective Java"));
        catalog.books.add(new CatalogBook("2", "Java Puzzlers"));
    }

    @Test
    void theAliasPointsToAFreshIndexWithEveryBook() {
        String index = reindexer.reindex();

        assertThat(index).startsWith("catalog-");
        assertThat(aliasTargets()).containsExactly(index);
        assertThat(countThroughAlias()).isEqualTo(2);
    }

    @Test
    void aSecondRunSwitchesTheAliasAndRemovesTheOldIndex() {
        String first = reindexer.reindex();
        catalog.books.add(new CatalogBook("3", "Spring in Action"));

        String second = reindexer.reindex();

        assertThat(second).isNotEqualTo(first);
        assertThat(aliasTargets()).containsExactly(second);
        assertThat(operations.indexOps(IndexCoordinates.of(first)).exists()).isFalse();
        assertThat(countThroughAlias()).isEqualTo(3);
    }

    @Test
    void theNewIndexUsesTheMappingOfTheDocument() {
        String index = reindexer.reindex();

        var mapping = operations.indexOps(IndexCoordinates.of(index)).getMapping();
        assertThat(typeOf(mapping, "title")).isEqualTo("text");
    }

    private List<String> aliasTargets() {
        return List.copyOf(operations.indexOps(IndexCoordinates.of(Reindexer.ALIAS))
                .getAliases(Reindexer.ALIAS).keySet());
    }

    private long countThroughAlias() {
        return operations.count(Query.findAll(), IndexCoordinates.of(Reindexer.ALIAS));
    }

    @SuppressWarnings("unchecked")
    private static Object typeOf(Map<String, Object> mapping, String field) {
        var properties = (Map<String, Object>) mapping.get("properties");
        return ((Map<String, Object>) properties.get(field)).get("type");
    }
}
