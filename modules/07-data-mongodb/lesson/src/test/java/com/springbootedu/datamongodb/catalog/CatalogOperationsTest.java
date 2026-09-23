package com.springbootedu.datamongodb.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.datamongodb.TestcontainersConfiguration;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;

/**
 * Lessons 3.3–3.5 — MongoTemplate queries and updates, aggregations and indexes.
 */
@DataMongoTest
@Import({TestcontainersConfiguration.class, CatalogOperations.class})
class CatalogOperationsTest {

    @Autowired
    BookRepository books;

    @Autowired
    PublisherRepository publishers;

    @Autowired
    CatalogOperations catalog;

    @BeforeEach
    void seed() {
        books.deleteAll();
        publishers.deleteAll();
        Publisher addison = publishers.save(new Publisher(null, "Addison-Wesley", "US"));
        Publisher manning = publishers.save(new Publisher(null, "Manning", "US"));
        books.saveAll(CatalogFixture.books(addison, manning));
    }

    @Test
    void criteriaQueryWithOptionalParts() {
        assertThat(catalog.search("java", new BigDecimal("90"))).extracting(Book::title)
                .containsExactly("Java Puzzlers", "Effective Java");
        assertThat(catalog.search(null, new BigDecimal("50"))).extracting(Book::title)
                .containsExactly("Kürk Mantolu Madonna");
    }

    @Test
    void atomicDecrementNeverGoesBelowZero() {
        assertThat(catalog.takeFromStock("9780321336781", 1)).isTrue();         // 1 → 0
        assertThat(catalog.takeFromStock("9780321336781", 1)).isFalse();        // no match: stock is 0
        assertThat(books.findByIsbn("9780321336781").orElseThrow().stock()).isZero();
    }

    @Test
    void pushAddsAReviewWithoutReadingTheDocument() {
        catalog.addReview("9781617297571", new Review("ali", 5, "Güncel ve net"));

        assertThat(books.findByIsbn("9781617297571").orElseThrow().reviews()).extracting(Review::author)
                .containsExactly("ali");
    }

    @Test
    void aggregationPerCategory() {
        assertThat(catalog.statsPerCategory()).containsExactly(
                new CategoryStats("java", 3, new BigDecimal("79.97")),
                new CategoryStats("best-practices", 1, new BigDecimal("89.90")),
                new CategoryStats("roman", 1, new BigDecimal("45.00")),
                new CategoryStats("spring", 1, new BigDecimal("95.00")));
    }

    @Test
    void theIsbnIndexIsUnique() {
        Book copy = new Book(null, "9780134685991", "Duplicate", List.of("X"), BigDecimal.ONE, 1, List.of(),
                Map.of(), null, List.of());

        assertThatThrownBy(() -> books.save(copy)).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void fullTextSearchUsesTheTextIndex() {
        assertThat(catalog.fullText("puzzlers")).extracting(Book::title).containsExactly("Java Puzzlers");
    }
}
