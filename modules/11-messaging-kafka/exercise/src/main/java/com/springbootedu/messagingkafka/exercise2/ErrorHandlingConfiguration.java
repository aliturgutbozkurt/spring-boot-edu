package com.springbootedu.messagingkafka.exercise2;

import org.springframework.context.annotation.Configuration;

/**
 * Exercise 2 — retry briefly, then send the record to "&lt;topic&gt;.DLT".
 */
@Configuration(proxyBeanMethods = false)
public class ErrorHandlingConfiguration {

    // TODO 2a: a DefaultErrorHandler bean — Boot adds it to every @KafkaListener
    // TODO 2b: its recoverer publishes failed records to "<original topic>.DLT" (use DeadLetterTemplates.create)
    // TODO 2c: retry twice, 100 ms apart
    // TODO 2d: an InvalidQuantityException is never retried
}
