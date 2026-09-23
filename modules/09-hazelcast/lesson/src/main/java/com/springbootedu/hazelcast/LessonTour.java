package com.springbootedu.hazelcast;

import com.hazelcast.client.Client;
import com.hazelcast.core.HazelcastInstance;
import com.springbootedu.hazelcast.catalog.Book;
import com.springbootedu.hazelcast.catalog.BookCatalog;
import com.springbootedu.hazelcast.pricing.PriceService;
import com.springbootedu.hazelcast.stock.StockService;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.function.Supplier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

/**
 * Runs every example of the lesson once, in the order of the lesson notes (section 3).
 * Start it with: ./mvnw -pl modules/09-hazelcast/lesson spring-boot:run
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final HazelcastInstance hazelcast;
    private final BookCatalog catalog;
    private final PriceService prices;
    private final StockService stock;

    LessonTour(HazelcastInstance hazelcast, BookCatalog catalog, PriceService prices, StockService stock) {
        this.hazelcast = hazelcast;
        this.catalog = catalog;
        this.prices = prices;
        this.stock = stock;
    }

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        boolean client = hazelcast.getLocalEndpoint() instanceof Client;

        section("3.1 Embedded or client?");
        int members = hazelcast.getCluster().getMembers().size();
        print((client ? "client connected to " : "embedded member in a cluster of ") + members + " member(s)");

        section("3.2 IMap");
        catalog.save(new Book("9780134685991", "Effective Java", new BigDecimal("89.90")));
        catalog.save(new Book("9780321336781", "Java Puzzlers", new BigDecimal("55.00")));
        catalog.saveFor(new Book("9781617297571", "Spring in Action", new BigDecimal("95.00")), Duration.ofSeconds(2));
        print("find: " + catalog.find("9780134685991").map(Book::title).orElse("-"));
        print("addIfAbsent again: " + catalog.addIfAbsent(new Book("9780134685991", "Other", BigDecimal.ONE)));

        section("3.3 Query");
        print("cheaper than 90: " + catalog.cheaperThan(new BigDecimal("90")).stream().map(Book::title).sorted().toList());
        Thread.sleep(2_500);
        print("Spring in Action after its 2 s TTL: " + catalog.find("9781617297571").map(Book::title).orElse("expired"));

        section("3.4 Spring Cache");
        print("1st price: " + timed(() -> prices.priceOf("9780134685991").toPlainString()));
        print("2nd price: " + timed(() -> prices.priceOf("9780134685991").toPlainString()));

        section("3.5 Stock: key lock and entry processor");
        stock.setAvailable("9780134685991", 3);
        print("reserve 2 with lock: " + stock.reserveWithLock("9780134685991", 2));
        if (client) {
            print("entry processor skipped: its class would have to be on the server's classpath (see 3.6)");
        } else {
            print("reserve 2 with entry processor: " + stock.reserveWithEntryProcessor("9780134685991", 2));
            print("reserve 1 with entry processor: " + stock.reserveWithEntryProcessor("9780134685991", 1));
        }
        print("left: " + stock.available("9780134685991"));
    }

    private static String timed(Supplier<String> work) {
        long start = System.nanoTime();
        String result = work.get();
        return result + " in " + (System.nanoTime() - start) / 1_000_000 + " ms";
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
