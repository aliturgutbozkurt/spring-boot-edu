package com.springbootedu.capstone.catalog.book;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.capstone.catalog.CatalogTest;
import com.springbootedu.capstone.catalog.TopicReader;
import com.springbootedu.capstone.catalog.Tokens;
import com.springbootedu.capstone.catalog.stock.StockReservations;
import com.springbootedu.capstone.contracts.events.BookChanged;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * C.4 — every change of a book, including its stock, is published as BookChanged (key = ISBN).
 */
@CatalogTest
class BookEventsTest {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    StockReservations reservations;

    @Autowired
    KafkaConnectionDetails kafka;

    String isbn = String.valueOf(ThreadLocalRandom.current().nextLong(1_000_000_000_000L, 9_999_999_999_999L));

    @Test
    void anAdminChangeIsPublished() {
        saveBook(7);

        List<String> events = TopicReader.valuesWithKey(kafka, BookChanged.TOPIC, isbn);

        assertThat(events).singleElement().asString()
                .contains("\"isbn\":\"" + isbn + "\"", "\"title\":\"Clean Code\"", "\"stock\":7")
                .doesNotContain("version");                     // the event is the contract, not the document
    }

    @Test
    void aReservationPublishesTheNewStock() {
        saveBook(7);
        reservations.reserve("order-" + isbn, Map.of(isbn, 3));

        List<String> events = TopicReader.valuesWithKey(kafka, BookChanged.TOPIC, isbn, 2);

        assertThat(events).hasSize(2).last().asString().contains("\"stock\":4");
    }

    private void saveBook(int stock) {
        assertThat(mvc.put().uri("/api/catalog/books/" + isbn).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", Tokens.bearer("admin", "ADMIN"))
                .content("""
                        {"title": "Clean Code", "authors": ["Robert C. Martin"], "description": "Readable code.",
                         "price": 70.00, "stock": %d}""".formatted(stock)))
                .hasStatusOk();
    }
}
