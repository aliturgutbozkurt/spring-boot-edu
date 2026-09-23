package com.springbootedu.elasticsearch.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.elasticsearch.TestcontainersConfiguration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.elasticsearch.test.autoconfigure.DataElasticsearchTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

/**
 * Exercise 2 — a shop search page: the hits follow every filter, the category counts do not hide other categories.
 */
@DataElasticsearchTest
@Import({TestcontainersConfiguration.class, FacetedSearch.class})
class Exercise2Test {

    @Autowired
    FacetedSearch search;

    @Autowired
    ElasticsearchOperations operations;

    @BeforeEach
    void seed() {
        var index = operations.indexOps(ProductDocument.class);
        index.delete();
        index.createWithMapping();
        operations.save(List.of(
                new ProductDocument("1", "Java notebook", "stationery", 20.0),
                new ProductDocument("2", "Java mug", "kitchen", 15.0),
                new ProductDocument("3", "Java book", "books", 90.0),
                new ProductDocument("4", "Java pocket guide", "books", 30.0),
                new ProductDocument("5", "Kotlin book", "books", 80.0)));
        index.refresh();
    }

    @Test
    void withoutFiltersTheHitsAndCountsCoverEveryMatch() {
        SearchPage page = search.search("java", null, null);

        assertThat(page.hits()).hasSize(4);
        assertThat(page.categories()).containsExactlyInAnyOrderEntriesOf(
                Map.of("books", 2L, "stationery", 1L, "kitchen", 1L));
    }

    @Test
    void theSelectedCategoryFiltersTheHitsButNotTheCounts() {
        SearchPage page = search.search("java", "books", null);

        assertThat(page.hits()).extracting(ProductDocument::name)
                .containsExactlyInAnyOrder("Java book", "Java pocket guide");
        assertThat(page.categories())                                  // the user can still switch category
                .containsEntry("stationery", 1L)
                .containsEntry("kitchen", 1L)
                .containsEntry("books", 2L);
    }

    @Test
    void thePriceLimitFiltersTheHitsAndTheCounts() {
        SearchPage page = search.search("java", null, 50.0);

        assertThat(page.hits()).hasSize(3);
        assertThat(page.categories()).containsEntry("books", 1L);      // the 90.00 book is not counted
    }

    @Test
    void bothFiltersTogether() {
        SearchPage page = search.search("java", "books", 50.0);

        assertThat(page.hits()).extracting(ProductDocument::name).containsExactly("Java pocket guide");
        assertThat(page.categories()).containsEntry("kitchen", 1L).containsEntry("books", 1L);
    }
}
