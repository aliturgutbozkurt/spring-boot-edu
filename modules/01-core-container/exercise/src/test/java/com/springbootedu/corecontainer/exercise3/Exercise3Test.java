package com.springbootedu.corecontainer.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Exercise 3 — publishing events and a filtered, ordered listener.
 */
class Exercise3Test {

    @Configuration(proxyBeanMethods = false)
    @ComponentScan(basePackageClasses = BookPublisher.class)
    static class ScanExercise3 {
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(ScanExercise3.class);

    @Test
    void publishingABookIsAudited() {
        runner.run(context -> {
            context.getBean(BookPublisher.class).publish("978-605-000-003-5", "Saatleri Ayarlama Enstitüsü", "Ahmet Hamdi Tanpınar");

            assertThat(context.getBean(Inbox.class).messages()).containsExactly("audit: 978-605-000-003-5");
        });
    }

    @Test
    void followersAreNotifiedBeforeTheAudit() {
        runner.run(context -> {
            context.getBean(FollowedAuthors.class).follow("Ahmet Hamdi Tanpınar");

            context.getBean(BookPublisher.class).publish("978-605-000-003-5", "Saatleri Ayarlama Enstitüsü", "Ahmet Hamdi Tanpınar");

            assertThat(context.getBean(Inbox.class).messages()).containsExactly(
                    "Takip ettiğiniz yazar / Followed author: Ahmet Hamdi Tanpınar — Saatleri Ayarlama Enstitüsü",
                    "audit: 978-605-000-003-5");
        });
    }

    @Test
    void booksOfOtherAuthorsDoNotNotifyFollowers() {
        runner.run(context -> {
            context.getBean(FollowedAuthors.class).follow("Sabahattin Ali");

            context.getBean(BookPublisher.class).publish("978-605-000-004-2", "Huzur", "Ahmet Hamdi Tanpınar");

            assertThat(context.getBean(Inbox.class).messages()).containsExactly("audit: 978-605-000-004-2");
        });
    }
}
