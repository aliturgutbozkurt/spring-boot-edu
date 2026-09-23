package com.springbootedu.datamongodb.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.datamongodb.TestcontainersConfiguration;
import java.math.BigDecimal;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Lessons 3.1–3.2 — document modelling and repository queries. MongoDB has no rollback in tests: clean first.
 */
@DataMongoTest
@Import(TestcontainersConfiguration.class)
class BookRepositoryTest {

    @Autowired
    BookRepository books;

    @Autowired
    PublisherRepository publishers;

    @Autowired
    MongoTemplate mongo;

    @BeforeEach
    void seed() {
        books.deleteAll();
        publishers.deleteAll();
        Publisher addison = publishers.save(new Publisher(null, "Addison-Wesley", "US"));
        Publisher manning = publishers.save(new Publisher(null, "Manning", "US"));
        books.saveAll(CatalogFixture.books(addison, manning));
    }

    @Test
    void reviewsAreEmbeddedAndThePublisherIsReferenced() {
        Document raw = mongo.getCollection("books").find(new Document("isbn", "9780134685991")).first();

        assertThat(raw.getList("reviews", Document.class)).hasSize(2);           // inside the book document
        assertThat(raw.get("publisher")).isInstanceOf(org.bson.types.ObjectId.class);   // only the id is stored
        assertThat(raw.get("price")).isInstanceOf(org.bson.types.Decimal128.class);     // exact decimal, not a string
    }

    @Test
    void theReferenceIsResolvedWhenReading() {
        Book book = books.findByIsbn("9780134685991").orElseThrow();

        assertThat(book.publisher().name()).isEqualTo("Addison-Wesley");
        assertThat(book.reviews()).extracting(Review::stars).containsExactly(5, 4);
    }

    @Test
    void derivedQueryOnAnArrayField() {
        assertThat(books.findByAuthorsContaining("Joshua Bloch")).extracting(Book::title)
                .containsExactlyInAnyOrder("Effective Java", "Java Puzzlers");
    }

    @Test
    void derivedQueryWithSorting() {
        assertThat(books.findByPriceLessThanOrderByPrice(new BigDecimal("90")))
                .extracting(Book::title)
                .containsExactly("Kürk Mantolu Madonna", "Java Puzzlers", "Effective Java");
    }

    @Test
    void jsonQueryOnADynamicAttribute() {
        assertThat(books.findByLanguage("tr")).extracting(Book::title).containsExactly("Kürk Mantolu Madonna");
    }
}
