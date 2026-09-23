package com.springbootedu.corecontainer.book;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Lesson 3.1 — setter injection for an optional collaborator.
 */
class ReportPrinterTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withBean(InMemoryBookCatalog.class)
            .withBean(ReportPrinter.class);

    @Test
    void worksWithoutTheOptionalFooter() {
        runner.run(context -> assertThat(context.getBean(ReportPrinter.class).print())
                .startsWith("Kitap sayısı / Book count: 4")
                .doesNotContain("---"));
    }

    @Test
    void usesTheFooterWhenABeanExists() {
        runner.withBean(ReportFooter.class, () -> () -> "--- Spring Boot Edu ---")
                .run(context -> assertThat(context.getBean(ReportPrinter.class).print())
                        .endsWith("--- Spring Boot Edu ---"));
    }
}
