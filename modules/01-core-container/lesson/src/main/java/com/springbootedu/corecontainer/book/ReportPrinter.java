package com.springbootedu.corecontainer.book;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.1 — setter injection, suitable for optional dependencies.
 */
// tag::setter-injection[]
@Component
public class ReportPrinter {

    private final BookCatalog catalog;                 // required → constructor
    private @Nullable ReportFooter footer;             // optional → setter

    public ReportPrinter(BookCatalog catalog) {
        this.catalog = catalog;
    }

    @Autowired(required = false)                       // skipped when no ReportFooter bean exists
    public void setFooter(ReportFooter footer) {
        this.footer = footer;
    }
    // end::setter-injection[]

    public String print() {
        var report = new StringBuilder("Kitap sayısı / Book count: " + catalog.findAll().size());
        catalog.findAll().forEach(book -> report.append("\n- ").append(book.title()));
        if (footer != null) {
            report.append('\n').append(footer.text());
        }
        return report.toString();
    }
}
