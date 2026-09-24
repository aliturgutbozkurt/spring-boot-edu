package com.springbootedu.nativeperformance.exercise1;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

/**
 * Exercise 1 — works on the JVM as it is. In a native image, three things would be missing: the format class
 * (reflection), the header file (resource) and the JSON binding of ImportedBook (reflection for Jackson).
 */
@Service
@ImportRuntimeHints(ExportHints.class)
@RegisterReflectionForBinding(ImportedBook.class)
public class ExportService {

    private final ExportFormat format;
    private final JsonMapper json;

    public ExportService(@Value("${bookstore.export.format}") String formatClassName, JsonMapper json) {
        this.format = instantiate(formatClassName);
        this.json = json;
    }

    public String export(String booksAsJson) {
        List<ImportedBook> books = List.of(json.readValue(booksAsJson, ImportedBook[].class));
        return header() + format.export(books);
    }

    private static ExportFormat instantiate(String className) {
        try {
            return (ExportFormat) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot create export format " + className, e);
        }
    }

    private static String header() {
        try (InputStream in = new ClassPathResource("export/header.txt").getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
