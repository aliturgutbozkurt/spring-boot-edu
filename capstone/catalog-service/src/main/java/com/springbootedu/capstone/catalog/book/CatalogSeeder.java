package com.springbootedu.capstone.catalog.book;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

/**
 * Fills an empty catalog with a few books, so that the platform can be tried at once.
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.catalog.seed", matchIfMissing = true)
class CatalogSeeder implements ApplicationRunner {

    private final BookRepository books;

    CatalogSeeder(BookRepository books) {
        this.books = books;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (books.count() > 0) {
            return;
        }
        books.saveAll(List.of(
                book("9780134685991", "Effective Java", "Joshua Bloch", "Best practices for the Java platform.", "89.90", 25),
                book("9781617297571", "Spring in Action", "Craig Walls", "Spring Framework and Spring Boot, hands-on.", "95.00", 10),
                book("9781449373320", "Designing Data-Intensive Applications", "Martin Kleppmann",
                        "Databases, replication, partitioning and stream processing.", "110.00", 8),
                book("9780596007126", "Head First Design Patterns", "Eric Freeman",
                        "Object-oriented design patterns with pictures and humour.", "75.00", 15),
                book("9781492078005", "Kafka: The Definitive Guide", "Gwen Shapira",
                        "Event streaming with Apache Kafka.", "99.00", 5)));
    }

    private static Book book(String isbn, String title, String author, String description, String price, int stock) {
        return new Book(isbn, title, List.of(author), description, new BigDecimal(price), stock, null);
    }
}
