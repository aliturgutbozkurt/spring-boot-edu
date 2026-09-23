package com.springbootedu.datajpapostgres.catalog;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lesson 3.5 — builds the query from the filled-in criteria only.
 */
@Service
@Transactional(readOnly = true)                            // read-only: no dirty checking, no flush
public class BookSearch {

    private final BookRepository books;

    public BookSearch(BookRepository books) {
        this.books = books;
    }

    // tag::search[]
    public Page<BookCard> search(BookFilter filter, Pageable pageable) {
        List<Specification<Book>> criteria = new ArrayList<>();
        if (filter.title() != null) {
            criteria.add(BookSpecifications.titleContains(filter.title()));
        }
        if (filter.maxPrice() != null) {
            criteria.add(BookSpecifications.priceAtMost(filter.maxPrice()));
        }
        if (filter.category() != null) {
            criteria.add(BookSpecifications.inCategory(filter.category()));
        }
        return books.findAll(Specification.allOf(criteria), pageable)
                .map(book -> new BookCard(book.getTitle(), book.getAuthor().getName(), book.getPrice()));
    }
    // end::search[]
}
