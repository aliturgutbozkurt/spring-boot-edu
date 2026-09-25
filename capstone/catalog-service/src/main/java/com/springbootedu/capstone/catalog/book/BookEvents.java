package com.springbootedu.capstone.catalog.book;

import com.springbootedu.capstone.contracts.events.BookChanged;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Architecture 3.2 — publishes BookChanged after every save of a book: admin changes, the seed data and
 * stock reservations all go through the repository, so none can be forgotten. The key is the ISBN, so the
 * changes of one book stay in order.
 * <p>
 * Trade-off: there is no outbox here. If Kafka is down, the search index misses the change until the book
 * changes again (the order service shows the safer outbox pattern, ADR-3).
 */
@Component
class BookEvents extends AbstractMongoEventListener<Book> {

    private static final Logger log = LoggerFactory.getLogger(BookEvents.class);

    private final KafkaTemplate<String, BookChanged> kafka;

    BookEvents(KafkaTemplate<String, BookChanged> kafka) {
        this.kafka = kafka;
    }

    // tag::book-events[]
    @Override
    public void onAfterSave(AfterSaveEvent<Book> event) {
        Book book = event.getSource();
        var changed = new BookChanged(book.isbn(), book.title(), book.authors(), book.description(), book.price(),
                book.stock());
        kafka.send(BookChanged.TOPIC, book.isbn(), changed)
                .whenComplete((result, failure) -> {
                    if (failure != null) {
                        log.warn("BookChanged for {} not published: {}", book.isbn(), failure.getMessage());
                    }
                });
    }
    // end::book-events[]

    @Configuration(proxyBeanMethods = false)
    static class Topics {

        @Bean
        NewTopic catalogTopic() {
            return TopicBuilder.name(BookChanged.TOPIC).partitions(3).build();
        }
    }
}
