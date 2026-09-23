package com.springbootedu.corecontainer;

import com.springbootedu.corecontainer.aop.MethodTimings;
import com.springbootedu.corecontainer.book.Book;
import com.springbootedu.corecontainer.book.BookService;
import com.springbootedu.corecontainer.book.ReportPrinter;
import com.springbootedu.corecontainer.condition.RecommendationService;
import com.springbootedu.corecontainer.configuration.PriceCalculator;
import com.springbootedu.corecontainer.configuration.TaxRate;
import com.springbootedu.corecontainer.event.NotificationLog;
import com.springbootedu.corecontainer.lifecycle.InventorySync;
import com.springbootedu.corecontainer.lifecycle.TitleIndex;
import com.springbootedu.corecontainer.registrar.NotificationService;
import com.springbootedu.corecontainer.scope.BrokenCheckoutService;
import com.springbootedu.corecontainer.scope.CheckoutService;
import java.math.BigDecimal;
import java.util.Arrays;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Runs every example of the lesson once, in the order of the lesson notes (section 3).
 * Start it with: ./mvnw -pl modules/01-core-container/lesson spring-boot:run
 */
@Component
class LessonTour implements ApplicationRunner {

    private final ApplicationContext context;

    LessonTour(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) {
        section("3.1 Constructor & setter injection");
        var books = context.getBean(BookService.class);
        print("Joshua Bloch: " + books.findByAuthor("Joshua Bloch").stream().map(Book::title).toList());
        print(context.getBean(ReportPrinter.class).print());

        section("3.2 @Configuration full vs lite mode");
        print("full mode shares the TaxRate bean: " + (context.getBean("fullModePriceCalculator", PriceCalculator.class).taxRate()
                == context.getBean("fullModeTaxRate", TaxRate.class)));
        print("lite mode shares the TaxRate bean: " + (context.getBean("liteModePriceCalculator", PriceCalculator.class).taxRate()
                == context.getBean("liteModeTaxRate", TaxRate.class)));

        section("3.3 Scopes");
        var broken = context.getBean(BrokenCheckoutService.class);
        var fixed = context.getBean(CheckoutService.class);
        print("prototype in singleton → same cart twice: " + (broken.startCheckout() == broken.startCheckout()));
        print("ObjectProvider → same cart twice: " + (fixed.startCheckout() == fixed.startCheckout()));

        section("3.4 Lifecycle");
        print("TitleIndex.search(\"java\") = " + context.getBean(TitleIndex.class).search("java"));
        print("InventorySync running: " + context.getBean(InventorySync.class).isRunning());

        section("3.5 Profiles & conditions");
        Environment env = context.getEnvironment();
        var recommendations = context.getBean(RecommendationService.class);
        print("active profiles: " + Arrays.toString(env.getActiveProfiles()));
        print(recommendations.getClass().getSimpleName() + " → "
                + recommendations.recommend().stream().map(Book::title).toList());

        section("3.6 BeanRegistrar");
        print(String.valueOf(context.getBean(NotificationService.class).broadcast("Kampanya başladı!")));

        section("3.7 Events");
        books.add(new Book("978-1-09-810339-4", "Spring Boot: Up and Running", "Mark Heckler", new BigDecimal("140.00")));
        context.getBean(NotificationLog.class).entries().forEach(LessonTour::print);

        section("3.8 AOP");
        print("measured methods: " + context.getBean(MethodTimings.class).recorded());
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
