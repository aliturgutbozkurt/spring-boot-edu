package com.springbootedu.testing.payment;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

/**
 * Lesson 3.6 — registers the PaymentClient bean; base URL and timeout come from application.yaml.
 */
@Configuration(proxyBeanMethods = false)
@ImportHttpServices(group = "payment", types = PaymentClient.class)
class PaymentClientConfiguration {
}
