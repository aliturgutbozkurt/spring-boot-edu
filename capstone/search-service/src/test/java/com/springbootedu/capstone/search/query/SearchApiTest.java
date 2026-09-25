package com.springbootedu.capstone.search.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.capstone.contracts.events.BookChanged;
import com.springbootedu.capstone.contracts.events.OrderPlaced;
import com.springbootedu.capstone.search.TestcontainersConfiguration;
import com.springbootedu.capstone.search.index.BookDocument;
import com.springbootedu.capstone.search.index.BookIndex;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

/**
 * C.3 — the search read model: indexing, searching, the Redis cache and idempotent sales counting.
 * Every test uses its own ISBN and its own made-up word, because the index is shared.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class SearchApiTest {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    BookIndex index;

    @Autowired
    ElasticsearchOperations elasticsearch;

    @Autowired
    CacheManager caches;

    String isbn = String.valueOf(ThreadLocalRandom.current().nextLong(1_000_000_000_000L, 9_999_999_999_999L));
    String word = "zeta" + UUID.randomUUID().toString().substring(0, 8);    // appears in no other book

    @BeforeEach
    void emptyTheCache() {
        caches.getCache("search").invalidate();
    }

    @Test
    void anIndexedBookIsFoundByTitleAuthorOrDescription() {
        index.index(book("Effective " + word, "Joshua Bloch", 5));

        assertThat(hits(word)).extracting(BookHit::isbn).containsExactly(isbn);
        assertThat(hits("bloch " + word)).extracting(BookHit::isbn).first().isEqualTo(isbn);
    }

    @Test
    void aSmallTypoStillFindsTheBook() {
        index.index(book("Kotlin " + word, "Anna", 5));

        String typo = word.substring(0, word.length() - 1) + "x";          // one letter wrong
        assertThat(hits(typo)).extracting(BookHit::isbn).contains(isbn);
    }

    @Test
    void resultsComeFromTheCacheUntilTheIndexChanges() {
        index.index(book("Old " + word, "Anna", 5));
        assertThat(hits(word)).extracting(BookHit::title).containsExactly("Old " + word);

        // change the document behind the service's back: the cached answer does not notice …
        elasticsearch.withRefreshPolicy(RefreshPolicy.IMMEDIATE).save(
                new BookDocument(isbn, "Sneaky " + word, List.of("Anna"), "", 10.0, 5, 0));
        assertThat(hits(word)).extracting(BookHit::title).containsExactly("Old " + word);

        // … but an update through the index evicts the cache
        index.index(book("New " + word, "Anna", 5));
        assertThat(hits(word)).extracting(BookHit::title).containsExactly("New " + word);
    }

    @Test
    void aSaleIsCountedOnceEvenWhenTheEventComesTwice() {
        index.index(book("Sold " + word, "Anna", 5));
        OrderPlaced order = order(3);

        index.recordSale(order);
        index.recordSale(order);                                             // Kafka delivers at least once

        assertThat(hits(word)).singleElement().extracting(BookHit::sold).isEqualTo(3L);
    }

    @Test
    void reindexingABookKeepsItsSales() {
        index.index(book("Kept " + word, "Anna", 5));
        index.recordSale(order(2));

        index.index(book("Kept " + word + " (2nd edition)", "Anna", 4));

        BookHit hit = hits(word).getFirst();
        assertThat(hit.sold()).isEqualTo(2L);
        assertThat(hit.inStock()).isTrue();
    }

    @Test
    void aBookWithoutStockIsMarked() {
        index.index(book("Gone " + word, "Anna", 0));

        assertThat(hits(word)).singleElement().extracting(BookHit::inStock).isEqualTo(false);
    }

    @Test
    void aSearchNeedsAQuery() {
        assertThat(mvc.get().uri("/api/search").param("q", " ")).hasStatus(HttpStatus.BAD_REQUEST);
    }

    private List<BookHit> hits(String q) {
        MvcTestResult result = mvc.get().uri("/api/search").param("q", q).exchange();
        assertThat(result).hasStatusOk();
        return List.of(assertThat(result).bodyJson().extractingPath("$.hits").convertTo(BookHit[].class).actual());
    }

    private BookChanged book(String title, String author, int stock) {
        return new BookChanged(isbn, title, List.of(author), "A book about " + title, new BigDecimal("42.00"), stock);
    }

    private OrderPlaced order(int quantity) {
        var line = new OrderPlaced.Line(isbn, "any", quantity, new BigDecimal("42.00"));
        return new OrderPlaced(UUID.randomUUID().toString(), "ayse", List.of(line),
                new BigDecimal("42.00").multiply(BigDecimal.valueOf(quantity)), Instant.now());
    }
}
