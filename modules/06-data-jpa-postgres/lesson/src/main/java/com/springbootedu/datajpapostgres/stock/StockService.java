package com.springbootedu.datajpapostgres.stock;

import com.springbootedu.datajpapostgres.catalog.Book;
import com.springbootedu.datajpapostgres.catalog.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lesson 3.7 — sells books without overselling, even when many buyers arrive at the same time.
 */
// tag::sell[]
@Service
public class StockService {

    private final BookRepository books;

    public StockService(BookRepository books) {
        this.books = books;
    }

    @Transactional
    public boolean trySell(String isbn, int quantity) {
        Book book = books.findForUpdate(isbn).orElseThrow();    // other buyers wait here until we commit
        if (book.getStock() < quantity) {
            return false;
        }
        book.removeStock(quantity);                             // written at commit (dirty checking)
        return true;
    }
}
// end::sell[]
