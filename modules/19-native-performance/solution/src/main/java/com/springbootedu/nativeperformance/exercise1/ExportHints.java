package com.springbootedu.nativeperformance.exercise1;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Exercise 1 — what ExportService needs in a native image.
 */
public class ExportHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        hints.reflection()
                .registerType(CsvExport.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
                .registerType(MarkdownExport.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
        hints.resources().registerPattern("export/header.txt");
    }
}
