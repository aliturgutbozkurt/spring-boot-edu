package com.springbootedu.elasticsearch.search;

import java.util.List;
import org.springframework.data.elasticsearch.core.SearchHit;

/**
 * Lesson 3.3 — one search result: the book, its relevance score and the highlighted fragments.
 */
public record BookHit(String id, String title, String author, String category, double price, float score,
                      List<String> highlights) {

    static BookHit from(SearchHit<BookDocument> hit) {
        BookDocument book = hit.getContent();
        List<String> highlights = hit.getHighlightFields().values().stream().flatMap(List::stream).toList();
        return new BookHit(book.id(), book.title(), book.author(), book.category(), book.price(), hit.getScore(),
                highlights);
    }
}
