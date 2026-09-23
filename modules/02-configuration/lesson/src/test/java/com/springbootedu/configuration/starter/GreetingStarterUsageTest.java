package com.springbootedu.configuration.starter;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.greeting.autoconfigure.GreetingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Lesson 3.6 — the application only added the starter dependency and two properties.
 */
@SpringBootTest
class GreetingStarterUsageTest {

    @Autowired
    GreetingService greetings;

    @Test
    void theStarterContributesAConfiguredBean() {
        assertThat(greetings.greet("Ayşe")).isEqualTo("Hoş geldiniz, Ayşe!");
    }
}
