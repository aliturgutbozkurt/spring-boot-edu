package com.springbootedu.nativeperformance.exercise1;

import java.util.List;

/**
 * Exercise 1 — one output format; the implementation is configured by class name.
 */
public interface ExportFormat {

    String export(List<ImportedBook> books);
}
