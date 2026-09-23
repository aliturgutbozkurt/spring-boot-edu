package com.springbootedu.corecontainer.book;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Lesson 3.1 — constructor injection lets us test without Spring: just call {@code new}.
 */
class BookServiceTest {

    private final InMemoryBookCatalog catalog = new InMemoryBookCatalog();
    private final List<Object> publishedEvents = new ArrayList<>();
    private final ApplicationEventPublisher publisher = new ApplicationEventPublisher() {
        @Override
        public void publishEvent(ApplicationEvent event) {
            publishedEvents.add(event);
        }

        @Override
        public void publishEvent(Object event) {
            publishedEvents.add(event);
        }
    };
    private final BookService service = new BookService(catalog, publisher);

    @Test
    void findsBooksOfAnAuthorSortedByTitle() {
        assertThat(service.findByAuthor("Joshua Bloch"))
                .extracting(Book::title)
                .containsExactly("Effective Java", "Java Puzzlers");
    }

    @Test
    void addStoresTheBookAndPublishesAnEvent() {
        var book = new Book("978-0-00-000000-1", "Yeni Kitap", "Ayşe Yılmaz", new BigDecimal("150.00"));

        service.add(book);

        assertThat(catalog.findAll()).contains(book);
        assertThat(publishedEvents).containsExactly(new BookAddedEvent(book));
    }
}
