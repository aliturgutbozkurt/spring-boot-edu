package com.springbootedu.corecontainer.lifecycle;

import com.springbootedu.corecontainer.book.Book;
import com.springbootedu.corecontainer.book.BookCatalog;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.4 — initialization and destruction callbacks.
 */
// tag::callbacks[]
@Component
public class TitleIndex {

    private final BookCatalog catalog;
    private final Map<String, String> titlesByLowerCase = new ConcurrentHashMap<>();

    public TitleIndex(BookCatalog catalog) {
        this.catalog = catalog;               // 1. constructor: dependencies are injected
    }

    @PostConstruct
    void build() {                            // 2. after injection: safe to use dependencies
        catalog.findAll().stream()
                .map(Book::title)
                .forEach(title -> titlesByLowerCase.put(title.toLowerCase(Locale.ROOT), title));
    }

    @PreDestroy
    void clear() {                            // 3. on context close: release resources
        titlesByLowerCase.clear();
    }
    // end::callbacks[]

    public List<String> search(String text) {
        String query = text.toLowerCase(Locale.ROOT);
        return titlesByLowerCase.entrySet().stream()
                .filter(entry -> entry.getKey().contains(query))
                .map(Map.Entry::getValue)
                .sorted()
                .toList();
    }

    public int size() {
        return titlesByLowerCase.size();
    }
}
