package com.springbootedu.reactive;

import com.springbootedu.reactive.basics.ReactorBasics;
import com.springbootedu.reactive.book.Book;
import com.springbootedu.reactive.book.BookRepository;
import com.springbootedu.reactive.compare.ConcurrencyComparison;
import java.time.Duration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

/**
 * Runs the lesson's examples once (section 3). Blocking with block() is fine here: this is a command-line demo.
 * Start it with: ./mvnw -pl modules/13-reactive/lesson spring-boot:run   (Docker must be running)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final BookRepository books;

    LessonTour(BookRepository books) {
        this.books = books;
    }

    @Override
    public void run(ApplicationArguments args) {
        var basics = new ReactorBasics();

        section("3.1 Mono and Flux");
        print("titles: " + basics.titlesInCapitals().collectList().block());
        print("prices (in parallel): " + basics.pricesOf("9780134685991", "9781617297571").collectList().block());

        section("3.2 Errors");
        print("unknown price with fallback: " + basics.priceOrFallback("unknown").block());
        print("flaky price after retries: " + basics.flakyPriceWithRetry().block());

        section("3.4 R2DBC");
        print("books in PostgreSQL: " + books.findAll().map(Book::title).collectList().block());

        section("3.7 Reactive vs. virtual threads (200 calls of 200 ms)");
        var comparison = new ConcurrencyComparison(Duration.ofMillis(200));
        print("reactive:        " + comparison.reactive(200).elapsed().toMillis() + " ms");
        print("virtual threads: " + comparison.virtualThreads(200).elapsed().toMillis() + " ms");

        section("3.5–3.6 HTTP");
        print("curl localhost:8080/api/books/9780134685991/details");
        print("curl -N localhost:8080/api/orders/stream   (then POST to /api/orders; Ctrl+C stops the app)");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
