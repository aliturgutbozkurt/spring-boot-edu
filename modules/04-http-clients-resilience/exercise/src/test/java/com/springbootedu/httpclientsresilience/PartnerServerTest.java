package com.springbootedu.httpclientsresilience;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Given: one WireMock server (per JVM) plays all partner services of the exercises.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class PartnerServerTest {

    protected static final WireMockServer partner = new WireMockServer(wireMockConfig().dynamicPort());

    static {
        partner.start();
    }

    @DynamicPropertySource
    static void partnerUrl(DynamicPropertyRegistry registry) {
        registry.add("bookstore.partner.base-url", partner::baseUrl);
        registry.add("spring.http.serviceclient.reviews.base-url", partner::baseUrl);
    }

    @BeforeEach
    void resetStubs() {
        partner.resetAll();
    }
}
