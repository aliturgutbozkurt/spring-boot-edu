package com.springbootedu.datajpapostgres.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.datajpapostgres.TestcontainersConfiguration;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Lesson 3.6 — auditing timestamps and optimistic locking with @Version.
 * Real commits are needed here, so the test-managed transaction is switched off.
 */
@DataJpaTest
@Import({TestcontainersConfiguration.class, com.springbootedu.datajpapostgres.JpaAuditingConfiguration.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class AuditingAndVersioningTest {

    @Autowired
    BookRepository books;

    @Autowired
    AuthorRepository authors;

    @Autowired
    PlatformTransactionManager transactionManager;

    @AfterEach
    void removeTestBook() {
        books.findByIsbn("9780132350884").ifPresent(books::delete);
    }

    private Book newBook() {
        Author author = authors.findByName("Martin Fowler").orElseThrow();
        return books.save(new Book("9780132350884", "Clean Code", author, new BigDecimal("75.50"), 3));
    }

    @Test
    void createdAndUpdatedTimestampsAreFilledIn() {
        Book saved = newBook();

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isEqualTo(saved.getCreatedAt());
        assertThat(saved.getVersion()).isZero();
    }

    @Test
    void everyUpdateIncrementsTheVersion() {
        Book saved = newBook();
        saved.changePrice(new BigDecimal("70.00"));

        Book updated = books.save(saved);

        assertThat(updated.getVersion()).isEqualTo(1);
        assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
    }

    @Test
    void aStaleCopyCannotOverwriteANewerOne() {
        Long id = newBook().getId();
        var tx = new TransactionTemplate(transactionManager);

        Book aliceCopy = tx.execute(status -> books.findById(id).orElseThrow());   // both read version 0
        Book bobCopy = tx.execute(status -> books.findById(id).orElseThrow());

        aliceCopy.changePrice(new BigDecimal("60.00"));
        books.save(aliceCopy);                                                  // version 0 → 1

        bobCopy.changePrice(new BigDecimal("65.00"));
        assertThatThrownBy(() -> books.save(bobCopy))                           // still thinks version 0
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }
}
