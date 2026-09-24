package com.springbootedu.nativeperformance.exercise1;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Exercise 1 — created by reflection only.
 */
public class MarkdownExport implements ExportFormat {

    @Override
    public String export(List<ImportedBook> books) {
        return books.stream()
                .map(book -> "| " + book.isbn() + " | " + book.title() + " | " + book.price() + " |")
                .collect(Collectors.joining("\n"));
    }
}
