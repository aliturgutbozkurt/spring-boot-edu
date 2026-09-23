package com.springbootedu.elasticsearch;

import com.springbootedu.elasticsearch.search.BookDocument;
import com.springbootedu.elasticsearch.search.BookIndexer;
import com.springbootedu.elasticsearch.search.BookSearch;
import com.springbootedu.elasticsearch.search.BookSearchRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

/**
 * Runs every example of the lesson once, in the order of the lesson notes (section 3).
 * Start it with: ./mvnw -pl modules/10-elasticsearch/lesson spring-boot:run   (Docker must be running)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final BookIndexer indexer;
    private final BookSearch search;
    private final BookSearchRepository repository;

    LessonTour(BookIndexer indexer, BookSearch search, BookSearchRepository repository) {
        this.indexer = indexer;
        this.search = search;
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        section("3.6 Index the books from PostgreSQL");
        print("indexed: " + indexer.reindexAll());

        section("3.2 Repository");
        print("by Joshua Bloch: " + repository.findByAuthor("Joshua Bloch").stream().map(BookDocument::title).toList());

        section("3.3 Full-text search");
        print("java: " + titles("java", null, null));
        print("kitap (text says \"kitaplar\"): " + titles("kitap", null, null));
        print("istanbul (text says \"İstanbul\"): " + titles("istanbul", null, null));
        print("java, programming, <= 60: " + titles("java", "programming", 60.0));
        print("highlight: " + search.search("polisiye", null, null).getFirst().highlights());

        section("3.4 Facets");
        print("java per category: " + search.categoryFacets("java"));

        section("3.5 Search API");
        print("curl 'localhost:8080/api/search?q=java&maxPrice=90'   (Ctrl+C stops the app)");
    }

    private String titles(String text, @Nullable String category, @Nullable Double maxPrice) {
        return search.search(text, category, maxPrice).stream()
                .map(hit -> hit.title() + " (" + String.format("%.2f", hit.score()) + ")").toList().toString();
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
