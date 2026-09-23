package com.springbootedu.corecontainer.condition;

import com.springbootedu.corecontainer.book.Book;
import com.springbootedu.corecontainer.book.BookCatalog;
import java.math.BigDecimal;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.5 — extra sample data, only for local development.
 */
// tag::profile[]
@Component
@Profile("dev")                               // run with: --spring.profiles.active=dev
public class DevSampleData implements ApplicationRunner {

    private final BookCatalog catalog;

    public DevSampleData(BookCatalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public void run(@Nullable ApplicationArguments args) {
        catalog.save(new Book("978-605-000-001-1", "Kürk Mantolu Madonna", "Sabahattin Ali", new BigDecimal("45.00")));
        catalog.save(new Book("978-605-000-002-8", "Tutunamayanlar", "Oğuz Atay", new BigDecimal("110.00")));
    }
}
// end::profile[]
