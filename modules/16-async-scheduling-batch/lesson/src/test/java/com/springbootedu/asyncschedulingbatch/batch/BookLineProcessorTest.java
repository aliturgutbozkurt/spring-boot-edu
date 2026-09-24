package com.springbootedu.asyncschedulingbatch.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

/**
 * Lesson 3.4 — the processor is plain Java: validation rules are unit-tested without a job.
 */
class BookLineProcessorTest {

    private final BookLineProcessor processor = new BookLineProcessor(new ImportFaults());

    @Test
    void aValidLineBecomesABook() {
        ImportedBook book = processor.process(new BookLine("9780134685991", " Effective Java ", "89.90"));

        assertThat(book.title()).isEqualTo("Effective Java");
        assertThat(book.price()).isEqualByComparingTo("89.90");
    }

    @Test
    void invalidLinesAreRejectedWithAnExplanation() {
        assertThatExceptionOfType(InvalidBookLineException.class)
                .isThrownBy(() -> processor.process(new BookLine("12345", "x", "10.00"))).withMessageContaining("ISBN");
        assertThatExceptionOfType(InvalidBookLineException.class)
                .isThrownBy(() -> processor.process(new BookLine("9780596007126", "x", "-5.00"))).withMessageContaining("price");
        assertThatExceptionOfType(InvalidBookLineException.class)
                .isThrownBy(() -> processor.process(new BookLine("9780596007126", "x", "ten"))).withMessageContaining("price");
    }
}
