package com.springbootedu.capstone.order.stock;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ADR-2 — how long a stock reservation may take before the order fails with 503.
 */
@ConfigurationProperties("bookstore.catalog")
public record CatalogProperties(Duration deadline) {
}
