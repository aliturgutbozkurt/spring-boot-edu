package com.springbootedu.nativeperformance.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;
import org.springframework.aot.test.generate.TestGenerationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.aot.ApplicationContextAotGenerator;
import tools.jackson.databind.json.JsonMapper;

class Exercise1Test {

    private static final String BOOKS = """
            [{"isbn": "9780134685991", "title": "Effective Java", "price": 89.90}]""";

    @Test
    void theExportWorksOnTheJvm() {
        var service = new ExportService(MarkdownExport.class.getName(), JsonMapper.builder().build());

        assertThat(service.export(BOOKS))
                .startsWith("Bookstore export")
                .endsWith("| 9780134685991 | Effective Java | 89.90 |");
    }

    @Test
    void theRegistrarCoversTheReflectionAndTheResource() {
        RuntimeHints hints = new RuntimeHints();
        new ExportHints().registerHints(hints, getClass().getClassLoader());

        assertThat(RuntimeHintsPredicates.reflection().onType(CsvExport.class)).accepts(hints);
        assertThat(RuntimeHintsPredicates.reflection().onType(MarkdownExport.class)).accepts(hints);
        assertThat(RuntimeHintsPredicates.resource().forResource("export/header.txt")).accepts(hints);
    }

    @Test
    void aotProcessingOfTheServiceCollectsAllHints() {
        RuntimeHints hints = hintsFromAotProcessing(ExportService.class);

        // only there if the registrar is registered for the service
        assertThat(RuntimeHintsPredicates.reflection().onType(CsvExport.class)).accepts(hints);
        // only there if Spring knows that ImportedBook is bound from JSON
        assertThat(RuntimeHintsPredicates.reflection().onType(ImportedBook.class)).accepts(hints);
    }

    /** Runs Spring's AOT processing (as process-aot does at build time) for a context with one bean. */
    private static RuntimeHints hintsFromAotProcessing(Class<?> component) {
        var context = new AnnotationConfigApplicationContext();
        context.register(component);
        var generationContext = new TestGenerationContext();
        new ApplicationContextAotGenerator().processAheadOfTime(context, generationContext);
        return generationContext.getRuntimeHints();
    }
}
