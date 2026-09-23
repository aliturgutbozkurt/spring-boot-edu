package com.springbootedu.datamongodb;

import com.springbootedu.datamongodb.catalog.Book;
import com.springbootedu.datamongodb.catalog.BookRepository;
import com.springbootedu.datamongodb.catalog.CatalogOperations;
import com.springbootedu.datamongodb.catalog.Publisher;
import com.springbootedu.datamongodb.catalog.PublisherRepository;
import com.springbootedu.datamongodb.catalog.Review;
import com.springbootedu.datamongodb.orders.Inventory;
import com.springbootedu.datamongodb.orders.Order;
import com.springbootedu.datamongodb.orders.OrderService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Runs every example of the lesson once, in the order of the lesson notes (section 3).
 * MongoDB has no migrations: the tour inserts sample data when the collection is empty.
 * Start it with: ./mvnw -pl modules/07-data-mongodb/lesson -am spring-boot:run   (Docker must be running)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final BookRepository books;
    private final PublisherRepository publishers;
    private final CatalogOperations catalog;
    private final OrderService orders;
    private final MongoTemplate mongo;

    LessonTour(BookRepository books, PublisherRepository publishers, CatalogOperations catalog, OrderService orders,
               MongoTemplate mongo) {
        this.books = books;
        this.publishers = publishers;
        this.catalog = catalog;
        this.orders = orders;
        this.mongo = mongo;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedIfEmpty();

        section("3.1 A document");
        Document raw = mongo.getCollection("books").find(new Document("isbn", "9780134685991")).first();
        print(raw == null ? "(missing)" : raw.toJson().replaceAll("\"_id\": \\{[^}]*}, ", ""));

        section("3.2 Repository queries");
        print("by author:    " + books.findByAuthorsContaining("Joshua Bloch").stream().map(Book::title).toList());
        print("by language:  " + books.findByLanguage("tr").stream().map(Book::title).toList());
        print("publisher:    " + books.findByIsbn("9780134685991").orElseThrow().publisher());

        section("3.3 MongoTemplate");
        print("java ≤ 90:    " + catalog.search("java", new BigDecimal("90")).stream().map(Book::title).toList());
        print("take 1 copy:  " + catalog.takeFromStock("9786050000011", 1)
                + ", stock now " + books.findByIsbn("9786050000011").orElseThrow().stock());

        section("3.4 Aggregation");
        catalog.statsPerCategory().forEach(stats -> print(stats.toString()));

        section("3.5 Text index");
        print("\"puzzlers\" → " + catalog.fullText("puzzlers").stream().map(Book::title).toList());

        section("3.6 Transaction");
        try {
            orders.place("ayse@example.com", "9781617297571", 99);
        } catch (IllegalStateException exception) {
            print("rolled back: " + exception.getMessage() + " — orders stored: "
                    + mongo.count(new Query(), Order.class));
        }
    }

    private void seedIfEmpty() {
        if (books.count() > 0) {
            return;
        }
        Publisher addison = publishers.save(new Publisher(null, "Addison-Wesley", "US"));
        Publisher manning = publishers.save(new Publisher(null, "Manning", "US"));
        books.saveAll(List.of(
                new Book(null, "9780134685991", "Effective Java", List.of("Joshua Bloch"), new BigDecimal("89.90"), 5,
                        List.of("java", "best-practices"), Map.of("language", "en", "pages", "412"), addison,
                        List.of(new Review("ayse", 5, "Harika"), new Review("mehmet", 4, "Çok iyi"))),
                new Book(null, "9780321336781", "Java Puzzlers", List.of("Joshua Bloch", "Neal Gafter"),
                        new BigDecimal("55.00"), 1, List.of("java"), Map.of("language", "en"), addison, List.of()),
                new Book(null, "9781617297571", "Spring in Action", List.of("Craig Walls"), new BigDecimal("95.00"), 4,
                        List.of("spring", "java"), Map.of("language", "en", "edition", "6"), manning, List.of()),
                new Book(null, "9786050000011", "Kürk Mantolu Madonna", List.of("Sabahattin Ali"),
                        new BigDecimal("45.00"), 10, List.of("roman"), Map.of("language", "tr"), null, List.of())));
        mongo.createCollection(Order.class);
        mongo.save(new Inventory("9781617297571", 4));
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
