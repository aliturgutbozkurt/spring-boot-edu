package com.springbootedu.springcloud;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.test.context.DynamicPropertyRegistry;

/**
 * Two fake instances of the catalog service, one pair per JVM (see CLAUDE.md: WireMock and cached contexts).
 */
public final class CatalogInstances {

    public static final WireMockServer CATALOG_1 = new WireMockServer(options().dynamicPort());
    public static final WireMockServer CATALOG_2 = new WireMockServer(options().dynamicPort());

    static {
        CATALOG_1.start();
        CATALOG_2.start();
    }

    private CatalogInstances() {
    }

    /** Registers both instances as "catalog-service" for Spring Cloud LoadBalancer. */
    public static void register(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.discovery.client.simple.instances.catalog-service[0].uri", CATALOG_1::baseUrl);
        registry.add("spring.cloud.discovery.client.simple.instances.catalog-service[1].uri", CATALOG_2::baseUrl);
        registry.add("spring.config.import", () -> "");                   // no Config Server in tests
    }

    public static void healthy() {
        answer(CATALOG_1, "catalog-1");
        answer(CATALOG_2, "catalog-2");
    }

    public static void failing() {
        for (WireMockServer instance : new WireMockServer[] {CATALOG_1, CATALOG_2}) {
            instance.resetAll();
            instance.stubFor(get(urlPathMatching("/api/books/.*")).willReturn(aResponse().withStatus(503)));
        }
    }

    private static void answer(WireMockServer instance, String name) {
        instance.resetAll();
        instance.stubFor(get(urlPathMatching("/api/books/.*")).willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("""
                        {"isbn": "9780134685991", "title": "Effective Java", "price": 89.90, "servedBy": "%s"}"""
                        .formatted(name))));
    }
}
