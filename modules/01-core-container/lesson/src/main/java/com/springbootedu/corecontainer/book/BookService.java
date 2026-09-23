package com.springbootedu.corecontainer.book;

import com.springbootedu.corecontainer.aop.LogExecutionTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Lesson 3.1 — constructor injection.
 */
// tag::constructor-injection[]
@Service
public class BookService {

    private final BookCatalog catalog;                 // final: set once, never null
    private final ApplicationEventPublisher events;

    // A single constructor needs no @Autowired: Spring calls it and passes the beans.
    public BookService(BookCatalog catalog, ApplicationEventPublisher events) {
        this.catalog = catalog;
        this.events = events;
    }
    // end::constructor-injection[]

    @LogExecutionTime
    public List<Book> findByAuthor(String author) {
        return catalog.findAll().stream()
                .filter(book -> book.author().equals(author))
                .sorted(Comparator.comparing(Book::title))
                .toList();
    }

    // tag::publish-event[]
    public void add(Book book) {
        catalog.save(book);
        events.publishEvent(new BookAddedEvent(book));   // listeners run after this line
    }
    // end::publish-event[]
}
