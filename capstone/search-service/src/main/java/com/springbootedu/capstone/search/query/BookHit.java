package com.springbootedu.capstone.search.query;

import com.springbootedu.capstone.search.index.BookDocument;
import java.util.List;

/**
 * One search result.
 */
public record BookHit(String isbn, String title, List<String> authors, double price, boolean inStock, long sold) {

    static BookHit from(BookDocument book) {
        return new BookHit(book.isbn(), book.title(), book.authors(), book.price(), book.stock() > 0, book.sold());
    }
}
