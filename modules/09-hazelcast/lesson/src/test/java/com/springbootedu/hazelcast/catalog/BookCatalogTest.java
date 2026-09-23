package com.springbootedu.hazelcast.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.math.BigDecimal;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Lessons 3.2–3.3 — an IMap is a distributed key-value map: TTL per entry, queries on the members.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
class BookCatalogTest {

    private static final Book EFFECTIVE_JAVA = new Book("9780134685991", "Effective Java", new BigDecimal("89.90"));
    private static final Book PUZZLERS = new Book("9780321336781", "Java Puzzlers", new BigDecimal("55.00"));
    private static final Book SPRING = new Book("9781617297571", "Spring in Action", new BigDecimal("95.00"));

    @Autowired
    BookCatalog catalog;

    @BeforeEach
    void reset() {
        catalog.clear();
    }

    @Test
    void storesAndReadsBooksByIsbn() {
        catalog.save(EFFECTIVE_JAVA);

        assertThat(catalog.find(EFFECTIVE_JAVA.isbn())).contains(EFFECTIVE_JAVA);
        assertThat(catalog.find("unknown")).isEmpty();
    }

    @Test
    void addIfAbsentKeepsTheFirstValue() {
        assertThat(catalog.addIfAbsent(EFFECTIVE_JAVA)).isTrue();
        assertThat(catalog.addIfAbsent(new Book(EFFECTIVE_JAVA.isbn(), "Other", BigDecimal.ONE))).isFalse();

        assertThat(catalog.find(EFFECTIVE_JAVA.isbn())).contains(EFFECTIVE_JAVA);
    }

    @Test
    void anEntryWithATimeToLiveDisappearsByItself() {
        catalog.saveFor(PUZZLERS, Duration.ofSeconds(1));

        assertThat(catalog.find(PUZZLERS.isbn())).isPresent();
        await().atMost(Duration.ofSeconds(5)).until(() -> catalog.find(PUZZLERS.isbn()).isEmpty());
    }

    @Test
    void queriesRunOnTheMembers() {
        catalog.save(EFFECTIVE_JAVA);
        catalog.save(PUZZLERS);
        catalog.save(SPRING);

        assertThat(catalog.cheaperThan(new BigDecimal("90.00")))
                .extracting(Book::title)
                .containsExactlyInAnyOrder("Effective Java", "Java Puzzlers");
    }
}
