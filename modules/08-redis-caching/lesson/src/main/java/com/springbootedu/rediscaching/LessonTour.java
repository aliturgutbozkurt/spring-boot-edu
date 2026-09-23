package com.springbootedu.rediscaching;

import com.springbootedu.rediscaching.catalog.BookService;
import com.springbootedu.rediscaching.pubsub.PriceChange;
import com.springbootedu.rediscaching.pubsub.PriceChangeListener;
import com.springbootedu.rediscaching.pubsub.PriceChangePublisher;
import com.springbootedu.rediscaching.structures.BestSellers;
import com.springbootedu.rediscaching.structures.PageViews;
import com.springbootedu.rediscaching.structures.RecentlyViewed;
import java.math.BigDecimal;
import java.util.function.Supplier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Runs every example of the lesson once, in the order of the lesson notes (section 3).
 * Start it with: ./mvnw -pl modules/08-redis-caching/lesson -am spring-boot:run   (Docker must be running)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final BookService books;
    private final StringRedisTemplate redis;
    private final BestSellers bestSellers;
    private final RecentlyViewed recentlyViewed;
    private final PageViews pageViews;
    private final PriceChangePublisher publisher;
    private final PriceChangeListener listener;

    LessonTour(BookService books, StringRedisTemplate redis, BestSellers bestSellers, RecentlyViewed recentlyViewed,
               PageViews pageViews, PriceChangePublisher publisher, PriceChangeListener listener) {
        this.books = books;
        this.redis = redis;
        this.bestSellers = bestSellers;
        this.recentlyViewed = recentlyViewed;
        this.pageViews = pageViews;
        this.publisher = publisher;
        this.listener = listener;
    }

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        section("3.1 @Cacheable");
        books.remove("9780321336781");                            // start without a cached entry
        books.changePrice("9780134685991", new BigDecimal("89.90"));
        redis.delete("books::9781617297571");
        print("1st find: " + timed(() -> books.find("9781617297571").title()));
        print("2nd find: " + timed(() -> books.find("9781617297571").title()));

        section("3.2 What is stored in Redis");
        print("books::9781617297571 = " + redis.opsForValue().get("books::9781617297571"));
        print("TTL: " + redis.getExpire("books::9781617297571") + " s");

        section("3.4 Sorted set and list");
        redis.delete("bestsellers");
        bestSellers.recordSale("Effective Java", 3);
        bestSellers.recordSale("Spring in Action", 5);
        bestSellers.recordSale("Effective Java", 4);
        print("top 2: " + bestSellers.top(2));
        for (int i = 1; i <= 7; i++) {
            recentlyViewed.view("demo", "isbn-" + i);
        }
        print("recently viewed: " + recentlyViewed.of("demo"));

        section("3.5 Counter");
        print("home page views today: " + pageViews.count("home"));

        section("3.6 Pub/Sub");
        publisher.publish(new PriceChange("9780134685991", new BigDecimal("79.90")));
        Thread.sleep(300);                                        // delivery is asynchronous
        print("listener received: " + listener.received());

        section("3.7 Spring Session");
        print("curl -c /tmp/c -b /tmp/c localhost:8080/api/visits   (run it twice; Ctrl+C stops the app)");
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
