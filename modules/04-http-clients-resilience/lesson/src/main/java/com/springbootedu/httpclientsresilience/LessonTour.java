package com.springbootedu.httpclientsresilience;

import com.springbootedu.httpclientsresilience.catalog.BookInfoNotFoundException;
import com.springbootedu.httpclientsresilience.catalog.CatalogApi;
import com.springbootedu.httpclientsresilience.catalog.CatalogRestClient;
import com.springbootedu.httpclientsresilience.catalog.ReactiveCatalogClient;
import com.springbootedu.httpclientsresilience.resilience.CoverService;
import com.springbootedu.httpclientsresilience.resilience.PriceService;
import java.util.concurrent.Executors;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

/**
 * Runs every example of the lesson once, in the order of the lesson notes (section 3).
 * The web server is already up when runners run, so the clients call the fake catalog on this very server.
 * Start it with: ./mvnw -pl modules/04-http-clients-resilience/lesson -am spring-boot:run
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final CatalogRestClient restClient;
    private final CatalogApi catalogApi;
    private final ReactiveCatalogClient reactiveClient;
    private final PriceService prices;
    private final CoverService covers;

    LessonTour(CatalogRestClient restClient, CatalogApi catalogApi, ReactiveCatalogClient reactiveClient,
               PriceService prices, CoverService covers) {
        this.restClient = restClient;
        this.catalogApi = catalogApi;
        this.reactiveClient = reactiveClient;
        this.prices = prices;
        this.covers = covers;
    }

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        section("3.1 RestClient");
        print(restClient.find("9780134685991").toString());

        section("3.2 Error handling");
        try {
            restClient.find("9780000000000");
        } catch (BookInfoNotFoundException exception) {
            print("BookInfoNotFoundException: " + exception.getMessage());
        }

        section("3.4 HTTP interface client");
        print(catalogApi.find("9781617297571").title());

        section("3.5 WebClient");
        print("Mono → " + reactiveClient.find("9780134685991").map(book -> book.pageCount() + " pages").block());

        section("3.6 @Retryable (the fake catalog fails twice, then answers)");
        long start = System.nanoTime();
        print("price " + prices.currentPrice("9780134685991").amount() + " after "
                + (System.nanoTime() - start) / 1_000_000 + " ms");

        section("3.7 @ConcurrencyLimit(2): 6 downloads of 200 ms each");
        start = System.nanoTime();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 6; i++) {
                executor.submit(() -> covers.download("9780134685991"));
            }
        }
        print("max in flight " + covers.maxInFlight() + ", took " + (System.nanoTime() - start) / 1_000_000 + " ms");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
