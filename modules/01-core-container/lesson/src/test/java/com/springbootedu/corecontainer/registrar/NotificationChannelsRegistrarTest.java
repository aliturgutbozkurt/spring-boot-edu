package com.springbootedu.corecontainer.registrar;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Lesson 3.6 — beans registered programmatically, decided at startup from configuration.
 */
class NotificationChannelsRegistrarTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(NotificationConfiguration.class);

    @Test
    void registersOnlyTheConfiguredChannels() {
        runner.withPropertyValues("bookstore.notifications.channels=email,sms")
                .run(context -> {
                    assertThat(context.getBeansOfType(NotificationChannel.class)).containsOnlyKeys("emailChannel", "smsChannel");
                    assertThat(context.getBean(NotificationService.class).broadcast("Merhaba"))
                            .containsExactly("[email] Merhaba", "[sms] Merhaba");
                });
    }

    @Test
    void fallsBackToEmailWhenNothingIsConfigured() {
        runner.run(context -> assertThat(context.getBeansOfType(NotificationChannel.class)).containsOnlyKeys("emailChannel"));
    }

    @Test
    void ignoresUnknownChannelNames() {
        runner.withPropertyValues("bookstore.notifications.channels=push,fax")
                .run(context -> assertThat(context.getBeansOfType(NotificationChannel.class)).containsOnlyKeys("pushChannel"));
    }
}
