package com.springbootedu.corecontainer.aop;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.corecontainer.book.BookService;
import com.springbootedu.corecontainer.book.InMemoryBookCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Lesson 3.8 — the aspect wraps annotated methods; callers see a proxy.
 */
class ExecutionTimeAspectTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AopAutoConfiguration.class))
            .withBean(InMemoryBookCatalog.class)
            .withBean(BookService.class)
            .withBean(MethodTimings.class)
            .withBean(ExecutionTimeAspect.class);

    @Test
    void recordsTheAnnotatedMethod() {
        runner.run(context -> {
            var service = context.getBean(BookService.class);
            assertThat(AopUtils.isAopProxy(service)).isTrue();

            service.findByAuthor("Joshua Bloch");

            assertThat(context.getBean(MethodTimings.class).recorded()).containsExactly("BookService.findByAuthor");
        });
    }

    @Test
    void ignoresMethodsWithoutTheAnnotation() {
        runner.run(context -> {
            context.getBean(BookService.class).add(new com.springbootedu.corecontainer.book.Book(
                    "978-0-00-000000-2", "Başka Kitap", "Yazar", java.math.BigDecimal.TEN));

            assertThat(context.getBean(MethodTimings.class).recorded()).isEmpty();
        });
    }
}
