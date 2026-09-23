package com.springbootedu.corecontainer.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Lesson 3.2 — {@code @Bean} registers objects of classes we cannot annotate (here: {@link Clock}).
 */
class AppConfigurationTest {

    @Test
    void registersAClockBean() {
        new ApplicationContextRunner()
                .withUserConfiguration(AppConfiguration.class)
                .run(context -> assertThat(context).hasSingleBean(Clock.class));
    }
}
