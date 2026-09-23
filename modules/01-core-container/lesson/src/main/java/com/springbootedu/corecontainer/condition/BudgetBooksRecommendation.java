package com.springbootedu.corecontainer.condition;

import com.springbootedu.corecontainer.book.Book;
import com.springbootedu.corecontainer.book.BookCatalog;
import java.util.Comparator;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Lesson 3.5 — active only when bookstore.recommendations.strategy=budget.
 */
@Service
@ConditionalOnProperty(name = "bookstore.recommendations.strategy", havingValue = "budget")
public class BudgetBooksRecommendation implements RecommendationService {

    private final BookCatalog catalog;

    public BudgetBooksRecommendation(BookCatalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public List<Book> recommend() {
        return catalog.findAll().stream().sorted(Comparator.comparing(Book::price)).limit(3).toList();
    }
}
