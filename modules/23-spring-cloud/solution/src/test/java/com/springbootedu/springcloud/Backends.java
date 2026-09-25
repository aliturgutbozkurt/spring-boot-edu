package com.springbootedu.springcloud;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.test.context.DynamicPropertyRegistry;

/**
 * Given — fake catalog and review services, one of each per JVM.
 */
public final class Backends {

    public static final WireMockServer CATALOG = new WireMockServer(options().dynamicPort());
    public static final WireMockServer REVIEWS = new WireMockServer(options().dynamicPort());

    static {
        CATALOG.start();
        REVIEWS.start();
        CATALOG.stubFor(get(urlPathMatching("/api/books/.*")).willReturn(json("{\"title\": \"Effective Java\"}")));
    }

    private Backends() {
    }

    public static void register(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.discovery.client.simple.instances.catalog-service[0].uri", CATALOG::baseUrl);
        registry.add("spring.cloud.discovery.client.simple.instances.review-service[0].uri", REVIEWS::baseUrl);
    }

    public static void reviewsHealthy() {
        REVIEWS.resetAll();
        REVIEWS.stubFor(get(urlPathMatching("/api/reviews/.*")).willReturn(json("{\"reviews\": [{\"stars\": 5}]}")));
    }

    public static void reviewsFailing() {
        REVIEWS.resetAll();
        REVIEWS.stubFor(get(urlPathMatching("/api/reviews/.*")).willReturn(aResponse().withStatus(503)));
    }

    private static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder json(String body) {
        return aResponse().withHeader("Content-Type", "application/json").withBody(body);
    }
}
