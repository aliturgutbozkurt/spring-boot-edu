package com.springbootedu.asyncschedulingbatch.batch;

import java.math.BigDecimal;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.3 — validates and converts one line. Throwing InvalidBookLineException skips the line.
 */
// tag::processor[]
@Component
public class BookLineProcessor implements ItemProcessor<BookLine, ImportedBook> {

    private final ImportFaults faults;

    public BookLineProcessor(ImportFaults faults) {
        this.faults = faults;
    }

    @Override
    public ImportedBook process(BookLine line) {
        faults.check(line.isbn());
        if (!line.isbn().matches("\\d{13}")) {
            throw new InvalidBookLineException("invalid ISBN: " + line.isbn());
        }
        BigDecimal price = parsePrice(line.price());
        if (price.signum() < 0) {
            throw new InvalidBookLineException("negative price for " + line.isbn());
        }
        return new ImportedBook(line.isbn(), line.title().trim(), price);
    }
    // end::processor[]

    private static BigDecimal parsePrice(String text) {
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidBookLineException("invalid price: " + text);
        }
    }
}
