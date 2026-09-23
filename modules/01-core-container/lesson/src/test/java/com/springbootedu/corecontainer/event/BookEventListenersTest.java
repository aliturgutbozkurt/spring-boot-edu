package com.springbootedu.corecontainer.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.corecontainer.book.Book;
import com.springbootedu.corecontainer.book.BookService;
import com.springbootedu.corecontainer.book.InMemoryBookCatalog;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Lesson 3.7 — listeners react to events without the publisher knowing about them.
 */
class BookEventListenersTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withBean(InMemoryBookCatalog.class)
            .withBean(BookService.class)
            .withBean(NotificationLog.class)
            .withBean(NewBookAnnouncer.class)
            .withBean(PremiumBookListener.class);

    @Test
    void everyListenerHearsAboutAPremiumBookInOrder() {
        runner.run(context -> {
            context.getBean(BookService.class).add(book("Spring Boot Up & Running", "150.00"));

            assertThat(context.getBean(NotificationLog.class).entries()).containsExactly(
                    "Yeni kitap / New book: Spring Boot Up & Running",
                    "Premium kitap / Premium book: Spring Boot Up & Running");
        });
    }

    @Test
    void theConditionFiltersCheapBooks() {
        runner.run(context -> {
            context.getBean(BookService.class).add(book("Pocket Java", "20.00"));

            assertThat(context.getBean(NotificationLog.class).entries())
                    .containsExactly("Yeni kitap / New book: Pocket Java");
        });
    }

    private static Book book(String title, String price) {
        return new Book("978-0-00-000000-9", title, "Test Author", new BigDecimal(price));
    }
}
