package com.springbootedu.setupmodernjava.textblocks;

import java.util.List;

/**
 * Lesson 3.4 — text blocks for multi-line strings, {@code var} for local variables.
 */
public final class CatalogExport {

    private CatalogExport() {
    }

    // tag::text-block[]
    public static String toJson(String title, String author, int year) {
        return """
                {
                  "title": "%s",
                  "author": "%s",
                  "year": %d
                }""".formatted(title, author, year);   // indentation left of the closing quotes is removed
    }
    // end::text-block[]

    // tag::var[]
    public static String receipt(List<String> titles) {
        var lines = new StringBuilder();                 // var: the type is obvious from the right-hand side
        for (var i = 0; i < titles.size(); i++) {
            lines.append(i + 1).append(". ").append(titles.get(i)).append('\n');
        }
        return """
                KİTAPÇI / BOOKSTORE
                %sToplam / Total: %d kitap / books
                """.formatted(lines, titles.size());
    }
    // end::var[]
}
