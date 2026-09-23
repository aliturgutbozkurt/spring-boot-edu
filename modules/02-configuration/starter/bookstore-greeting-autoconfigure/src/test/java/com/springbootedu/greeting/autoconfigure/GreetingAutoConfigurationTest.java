package com.springbootedu.greeting.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Lesson 3.6 — every condition of an auto-configuration deserves a test.
 */
class GreetingAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GreetingAutoConfiguration.class));

    @Test
    void createsAGreetingServiceWithDefaults() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(GreetingService.class);
            assertThat(context.getBean(GreetingService.class).greet("Ayşe")).isEqualTo("Merhaba, Ayşe!");
        });
    }

    @Test
    void propertiesCustomiseTheGreeting() {
        runner.withPropertyValues("bookstore.greeting.prefix=Hoş geldin", "bookstore.greeting.suffix=.")
                .run(context -> assertThat(context.getBean(GreetingService.class).greet("Ayşe")).isEqualTo("Hoş geldin, Ayşe."));
    }

    @Test
    void backsOffWhenTheApplicationDefinesItsOwnService() {
        runner.withUserConfiguration(CustomGreeting.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(GreetingService.class);
                    assertThat(context.getBean(GreetingService.class).greet("Ayşe")).isEqualTo("Hi Ayşe");
                });
    }

    @Test
    void canBeSwitchedOff() {
        runner.withPropertyValues("bookstore.greeting.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(GreetingService.class));
    }

    @Test
    void doesNothingWhenTheServiceClassIsMissing() {
        runner.withClassLoader(new FilteredClassLoader(GreetingService.class))
                .run(context -> assertThat(context).doesNotHaveBean("greetingService"));
    }

    @Test
    void isListedInTheAutoConfigurationImportsFile() throws IOException {
        var imports = new ClassPathResource(
                "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        assertThat(imports.getContentAsString(StandardCharsets.UTF_8)).contains(GreetingAutoConfiguration.class.getName());
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomGreeting {

        @Bean
        GreetingService customGreetingService() {
            return name -> "Hi " + name;
        }
    }
}
