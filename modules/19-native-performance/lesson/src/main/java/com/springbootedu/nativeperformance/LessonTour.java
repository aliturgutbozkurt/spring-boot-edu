package com.springbootedu.nativeperformance;

import com.springbootedu.nativeperformance.book.BookRepository;
import com.springbootedu.nativeperformance.price.PriceFormats;
import java.time.Duration;
import org.springframework.aot.AotDetector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.NativeDetector;
import org.springframework.stereotype.Component;

/**
 * Prints how the application runs and how long the start took (section 3). The comparison script reads this line.
 * Start it with: ./mvnw -pl modules/19-native-performance/lesson spring-boot:run   (Docker must be running)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour {

    private final BookRepository books;
    private final PriceFormats prices;

    LessonTour(BookRepository books, PriceFormats prices) {
        this.books = books;
        this.prices = prices;
    }

    // tag::mode[]
    @EventListener
    void ready(ApplicationReadyEvent event) {
        String mode = NativeDetector.inNativeImage() ? "native image"
                : AotDetector.useGeneratedArtifacts() ? "JVM with Spring AOT" : "JVM";
        Duration startup = event.getTimeTaken();
        System.out.println("=== mode: " + mode + ", ready in " + startup.toMillis() + " ms ===");
        books.publishedSince(2017).forEach(book ->
                System.out.println("  " + book.title() + " — " + prices.format(book.price())));
    }
    // end::mode[]
}
