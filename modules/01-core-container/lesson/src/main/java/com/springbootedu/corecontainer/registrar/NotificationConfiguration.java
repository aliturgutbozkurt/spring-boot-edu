package com.springbootedu.corecontainer.registrar;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Lesson 3.6 — a {@code BeanRegistrar} is activated with {@code @Import}.
 */
// tag::import-registrar[]
@Configuration(proxyBeanMethods = false)
@Import(NotificationChannelsRegistrar.class)
public class NotificationConfiguration {

    @Bean
    NotificationService notificationService(List<NotificationChannel> channels) {
        return new NotificationService(channels);
    }
}
// end::import-registrar[]
