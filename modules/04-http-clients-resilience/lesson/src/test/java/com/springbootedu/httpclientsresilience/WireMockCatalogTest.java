package com.springbootedu.httpclientsresilience;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base class: one WireMock server plays the remote catalog service; the application's base URLs point to it.
 *
 * The server is started once per JVM and never restarted. A JUnit extension would restart it per test class
 * on a new random port, while Spring caches the application context with the old URL.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"bookstore.tour.enabled=false", "bookstore.catalog.fake-server.enabled=false"})
public abstract class WireMockCatalogTest {

    protected static final WireMockServer catalog = new WireMockServer(wireMockConfig().dynamicPort());

    static {
        catalog.start();
    }

    @DynamicPropertySource
    static void catalogUrl(DynamicPropertyRegistry registry) {
        registry.add("bookstore.catalog.base-url", catalog::baseUrl);
        registry.add("spring.http.serviceclient.catalog.base-url", catalog::baseUrl);
    }

    @BeforeEach
    void resetStubs() {
        catalog.resetAll();                  // every test starts without stubs and without recorded requests
    }

    protected static final String EFFECTIVE_JAVA = """
            {"isbn": "9780134685991", "title": "Effective Java", "authors": ["Joshua Bloch"], "pageCount": 412}""";
}
