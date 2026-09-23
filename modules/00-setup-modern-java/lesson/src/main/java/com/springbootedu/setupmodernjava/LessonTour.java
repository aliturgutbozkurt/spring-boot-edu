package com.springbootedu.setupmodernjava;

import com.springbootedu.setupmodernjava.collections.RecentlyViewed;
import com.springbootedu.setupmodernjava.gatherers.SalesStatistics;
import com.springbootedu.setupmodernjava.patterns.Book;
import com.springbootedu.setupmodernjava.patterns.Format;
import com.springbootedu.setupmodernjava.patterns.OrderLine;
import com.springbootedu.setupmodernjava.patterns.OrderLinePricing;
import com.springbootedu.setupmodernjava.records.Isbn;
import com.springbootedu.setupmodernjava.records.Money;
import com.springbootedu.setupmodernjava.scopedvalues.AuditLog;
import com.springbootedu.setupmodernjava.scopedvalues.CheckoutFlow;
import com.springbootedu.setupmodernjava.scopedvalues.CurrentCustomer;
import com.springbootedu.setupmodernjava.sealed.Discount;
import com.springbootedu.setupmodernjava.sealed.Discounts;
import com.springbootedu.setupmodernjava.sealed.FixedDiscount;
import com.springbootedu.setupmodernjava.sealed.NoDiscount;
import com.springbootedu.setupmodernjava.sealed.PercentageDiscount;
import com.springbootedu.setupmodernjava.textblocks.CatalogExport;
import com.springbootedu.setupmodernjava.virtualthreads.PriceLookup;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Runs every example of the lesson once, in the order of the lesson notes (section 3).
 * Start it with: ./mvnw -pl modules/00-setup-modern-java/lesson spring-boot:run
 */
@Component
class LessonTour implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        section("3.0 Environment");
        print("Java " + Runtime.version().feature() + " · " + System.getProperty("java.vendor"));

        section("3.1 Records");
        print("Money.of(\"12.5\") = " + Money.of("12.5") + ", ISBN = " + new Isbn("978-0-13-468599-1").value());

        section("3.2 Sealed types & exhaustive switch");
        for (Discount discount : List.<Discount>of(new PercentageDiscount(10), new FixedDiscount(Money.of("30")), new NoDiscount())) {
            print(Discounts.describe(discount) + " → " + Discounts.apply(discount, Money.of("200")));
        }

        section("3.3 Record patterns");
        var paperback = new Book("Effective Java", Money.of("100"), Format.PAPERBACK);
        var ebook = new Book("Java Puzzlers", Money.of("40"), Format.EBOOK);
        print("10 × paperback = " + OrderLinePricing.total(new OrderLine(paperback, 10)));
        print("10 × e-book    = " + OrderLinePricing.total(new OrderLine(ebook, 10)));

        section("3.4 Text blocks & var");
        print(CatalogExport.toJson("Effective Java", "Joshua Bloch", 2018));

        section("3.5 Virtual threads");
        var result = PriceLookup.lookupAll(10_000, Duration.ofMillis(100));
        print(result.completed() + " blocking calls of 100 ms took " + result.elapsed().toMillis() + " ms");

        section("3.6 Scoped values");
        var log = new AuditLog();
        CurrentCustomer.runAs("ayse@example.com", () -> new CheckoutFlow(log).checkout("978-0-13-468599-1"));
        new CheckoutFlow(log).checkout("978-1-61729-757-1");
        log.entries().forEach(LessonTour::print);

        section("3.7 Sequenced collections");
        var recent = new RecentlyViewed(3);
        List.of("A", "B", "C", "A", "D").forEach(recent::view);
        print("newest first " + recent.newestFirst() + ", oldest first " + recent.oldestFirst());

        section("3.8 Stream gatherers");
        print("batches " + SalesStatistics.batches(List.of("a", "b", "c", "d", "e"), 2));
        print("moving average " + SalesStatistics.movingAverage(List.of(10, 20, 30, 40), 3));
        print("running total " + SalesStatistics.runningTotal(List.of(5, 10, 20)));
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
