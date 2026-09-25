package com.springbootedu.grpc.catalog;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Lesson 3.6 — bookstore.catalog.latency simulates a slow backend, to see deadlines at work.
 */
@ConfigurationProperties("bookstore.catalog")
public record CatalogProperties(@DefaultValue("0ms") Duration latency) {
}
