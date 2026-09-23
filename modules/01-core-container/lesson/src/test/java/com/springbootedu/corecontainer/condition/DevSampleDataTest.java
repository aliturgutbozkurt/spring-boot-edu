package com.springbootedu.corecontainer.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.corecontainer.book.InMemoryBookCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Lesson 3.5 — a bean that exists only when the "dev" profile is active.
 */
class DevSampleDataTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withBean(InMemoryBookCatalog.class)
            .withUserConfiguration(DevSampleData.class);

    @Test
    void isAbsentWithoutTheDevProfile() {
        runner.run(context -> assertThat(context).doesNotHaveBean(DevSampleData.class));
    }

    @Test
    void addsSampleBooksWhenTheDevProfileIsActive() {
        runner.withInitializer(context -> context.getEnvironment().setActiveProfiles("dev"))
                .run(context -> {
                    assertThat(context).hasSingleBean(DevSampleData.class);
                    context.getBean(DevSampleData.class).run(null);
                    assertThat(context.getBean(InMemoryBookCatalog.class).findAll()).hasSize(6);
                });
    }
}
