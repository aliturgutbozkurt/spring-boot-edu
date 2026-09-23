package com.springbootedu.elasticsearch.search;

import com.springbootedu.elasticsearch.catalog.BookSaved;
import com.springbootedu.elasticsearch.catalog.BookStore;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Lesson 3.6 — keeps the search index in step with PostgreSQL.
 */
// tag::indexer[]
@Component
public class BookIndexer {

    private final BookSearchRepository index;
    private final BookStore store;

    public BookIndexer(BookSearchRepository index, BookStore store) {
        this.index = index;
        this.store = store;
    }

    @TransactionalEventListener                                         // default phase: AFTER_COMMIT
    public void onBookSaved(BookSaved event) {
        index.save(BookDocument.from(event.book()));                   // rolled-back books never get here
    }

    public long reindexAll() {                                          // rebuild the index from the source of truth
        index.deleteAll();
        index.saveAll(store.findAll().stream().map(BookDocument::from).toList());
        return index.count();
    }
}
// end::indexer[]
