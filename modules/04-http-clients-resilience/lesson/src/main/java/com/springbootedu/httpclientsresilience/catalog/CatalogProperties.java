package com.springbootedu.httpclientsresilience.catalog;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Where the remote catalog service lives.
 *
 * @param baseUrl e.g. http://localhost:8080
 */
@ConfigurationProperties("bookstore.catalog")
public record CatalogProperties(String baseUrl) {
}
