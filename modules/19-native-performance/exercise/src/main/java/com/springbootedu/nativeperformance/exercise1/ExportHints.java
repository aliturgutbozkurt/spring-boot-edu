package com.springbootedu.nativeperformance.exercise1;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Exercise 1 — what ExportService needs in a native image.
 */
public class ExportHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        // TODO 1a: allow creating CsvExport and MarkdownExport by reflection, and include export/header.txt
    }
}
