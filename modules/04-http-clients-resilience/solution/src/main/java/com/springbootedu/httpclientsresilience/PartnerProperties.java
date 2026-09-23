package com.springbootedu.httpclientsresilience;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Given: where the partner services live.
 *
 * @param baseUrl e.g. http://partner.example
 */
@ConfigurationProperties("bookstore.partner")
public record PartnerProperties(String baseUrl) {
}
