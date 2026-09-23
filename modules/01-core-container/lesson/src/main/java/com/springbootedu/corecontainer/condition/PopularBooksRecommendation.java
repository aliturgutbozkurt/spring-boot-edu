package com.springbootedu.corecontainer.condition;

import com.springbootedu.corecontainer.book.Book;
import com.springbootedu.corecontainer.book.BookCatalog;
import java.util.Comparator;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Lesson 3.5 — active when the property is "popular" or missing.
 */
// tag::conditional-on-property[]
@Service
@ConditionalOnProperty(name = "bookstore.recommendations.strategy", havingValue = "popular", matchIfMissing = true)
public class PopularBooksRecommendation implements RecommendationService {
    // end::conditional-on-property[]

    private final BookCatalog catalog;

    public PopularBooksRecommendation(BookCatalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public List<Book> recommend() {
        // "Popular" is simplified to "most expensive" — this lesson is about the condition, not the algorithm.
        return catalog.findAll().stream().sorted(Comparator.comparing(Book::price).reversed()).limit(3).toList();
    }
}
