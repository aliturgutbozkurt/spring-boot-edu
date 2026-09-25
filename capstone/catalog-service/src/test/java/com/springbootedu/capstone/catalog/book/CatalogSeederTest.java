package com.springbootedu.capstone.catalog.book;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.capstone.catalog.CatalogTest;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;

/**
 * Two catalog instances start at the same time on an empty database (Kubernetes, 2 replicas):
 * both seed, neither may fail, and every book exists once.
 */
@CatalogTest
class CatalogSeederTest {

    @Autowired
    BookRepository books;

    @Test
    void twoInstancesSeedingAtOnceBothStart() {
        books.deleteAll();
        var first = new CatalogSeeder(books);
        var second = new CatalogSeeder(books);

        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> first.run(new DefaultApplicationArguments())),
                CompletableFuture.runAsync(() -> second.run(new DefaultApplicationArguments()))).join();

        assertThat(books.count()).isEqualTo(5);
    }
}
