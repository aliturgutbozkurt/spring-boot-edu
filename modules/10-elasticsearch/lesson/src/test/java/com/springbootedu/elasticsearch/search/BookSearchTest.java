package com.springbootedu.elasticsearch.search;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.elasticsearch.TestcontainersConfiguration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.elasticsearch.test.autoconfigure.DataElasticsearchTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

/**
 * Lessons 3.1–3.4 — mapping, analyzers, full-text queries, filters, highlighting and facets.
 */
@DataElasticsearchTest
@Import({TestcontainersConfiguration.class, BookSearch.class})
class BookSearchTest {

    @Autowired
    BookSearchRepository repository;

    @Autowired
    BookSearch search;

    @Autowired
    ElasticsearchOperations operations;

    @BeforeEach
    void seed() {
        repository.deleteAll();
        repository.saveAll(List.of(
                new BookDocument("1", "Effective Java", "Joshua Bloch",
                        "Java dilinde etkili programlama için doksan kural.", "programming", 89.90),
                new BookDocument("2", "Spring in Action", "Craig Walls",
                        "Spring Boot ile modern Java uygulamaları geliştirme.", "programming", 95.00),
                new BookDocument("3", "Clean Architecture", "Robert C. Martin",
                        "Yazılım mimarisi üzerine kitaplar arasında bir klasik.", "architecture", 75.00),
                new BookDocument("4", "İstanbul Hatırası", "Ahmet Ümit",
                        "İstanbul sokaklarında geçen bir polisiye roman.", "novel", 45.00),
                new BookDocument("5", "Java Puzzlers", "Joshua Bloch",
                        "Java'nın şaşırtıcı köşeleri ve tuzakları.", "programming", 55.00)));
        operations.indexOps(BookDocument.class).refresh();          // make the new documents searchable now
    }

    @Test
    void fullTextSearchLooksAtTitleAndDescription() {
        assertThat(search.search("mimarisi", null, null)).extracting(BookHit::title)
                .containsExactly("Clean Architecture");
        assertThat(search.search("spring", null, null)).extracting(BookHit::title)
                .containsExactly("Spring in Action");
    }

    @Test
    void aMatchInTheTitleRanksHigherThanInTheDescription() {
        List<BookHit> hits = search.search("java", null, null);

        assertThat(hits).extracting(BookHit::title).hasSize(3);
        assertThat(hits.subList(0, 2)).extracting(BookHit::title)
                .containsExactlyInAnyOrder("Effective Java", "Java Puzzlers");    // "Java" in the title (×3)
        assertThat(hits.getLast().title()).isEqualTo("Spring in Action");        // only in the description
    }

    @Test
    void theTurkishAnalyzerMatchesInflectedWords() {
        assertThat(search.search("kitap", null, null)).extracting(BookHit::title)
                .containsExactly("Clean Architecture");                // the text says "kitaplar"
    }

    @Test
    void theTurkishAnalyzerLowercasesTheDottedCapitalI() {
        assertThat(search.search("istanbul", null, null)).extracting(BookHit::title)
                .containsExactly("İstanbul Hatırası");                 // "İstanbul" → "istanbul"
    }

    @Test
    void filtersNarrowTheResult() {
        assertThat(search.search("java", "programming", 60.0)).extracting(BookHit::title)
                .containsExactly("Java Puzzlers");
    }

    @Test
    void highlightsMarkTheMatchingWords() {
        BookHit hit = search.search("polisiye", null, null).getFirst();

        assertThat(hit.highlights()).anySatisfy(fragment -> assertThat(fragment).contains("<em>polisiye</em>"));
    }

    @Test
    void facetsCountTheMatchesPerCategory() {
        assertThat(search.categoryFacets("java"))
                .containsEntry("programming", 3L)
                .doesNotContainKey("novel");
    }

    @Test
    void derivedQueriesWorkLikeInOtherSpringDataModules() {
        assertThat(repository.findByAuthor("Joshua Bloch")).extracting(BookDocument::title)
                .containsExactlyInAnyOrder("Effective Java", "Java Puzzlers");
    }
}
