package com.springbootedu.configuration.diagnostics;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Lesson 3.7 — the conditions report explains why an auto-configuration did (not) apply.
 */
@SpringBootTest
class ConditionsExplainerTest {

    @Autowired
    ConditionsExplainer explainer;

    @Test
    void explainsWhyTheGreetingAutoConfigurationMatched() {
        assertThat(explainer.explain("GreetingAutoConfiguration"))
                .startsWith("MATCHED")
                .contains("@ConditionalOnClass found required class");
    }

    @Test
    void explainsWhyAnAutoConfigurationWasSkipped() {
        assertThat(explainer.explain("MessageSourceAutoConfiguration"))
                .startsWith("SKIPPED")
                .contains("ResourceBundle");                  // no messages.properties in this module
    }

    @Test
    void autoConfigurationsOfMissingModulesAreNotEvenCandidates() {
        // Boot 4 is modular: DataSourceAutoConfiguration lives in spring-boot-jdbc, which this module lacks.
        assertThat(explainer.explain("DataSourceAutoConfiguration")).startsWith("NOT A CANDIDATE");
    }
}
