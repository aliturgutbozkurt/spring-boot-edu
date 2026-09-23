package com.springbootedu.configuration;

import com.springbootedu.configuration.diagnostics.ConditionsExplainer;
import com.springbootedu.configuration.imports.CampaignProperties;
import com.springbootedu.configuration.legacy.StoreInfo;
import com.springbootedu.configuration.sources.PropertyOrigins;
import com.springbootedu.configuration.store.StoreProperties;
import com.springbootedu.greeting.autoconfigure.GreetingService;
import java.util.Arrays;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Runs every example of the lesson once, in the order of the lesson notes (section 3).
 * Start it with: ./mvnw -pl modules/02-configuration/lesson -am spring-boot:run
 */
@Component
class LessonTour implements ApplicationRunner {

    private final StoreProperties store;
    private final PropertyOrigins origins;
    private final StoreInfo storeInfo;
    private final Environment environment;
    private final CampaignProperties campaigns;
    private final GreetingService greetings;
    private final ConditionsExplainer conditions;

    LessonTour(StoreProperties store, PropertyOrigins origins, StoreInfo storeInfo, Environment environment,
               CampaignProperties campaigns, GreetingService greetings, ConditionsExplainer conditions) {
        this.store = store;
        this.origins = origins;
        this.storeInfo = storeInfo;
        this.environment = environment;
        this.campaigns = campaigns;
        this.greetings = greetings;
        this.conditions = conditions;
    }

    @Override
    public void run(ApplicationArguments args) {
        section("3.1 Property sources");
        print("bookstore.store.name = " + store.name() + "  ← " + origins.sourceOf("bookstore.store.name"));
        print("bookstore.store.support-email ← " + origins.sourceOf("bookstore.store.support-email"));

        section("3.2 @ConfigurationProperties");
        print("categories " + store.categories() + ", currency " + store.currency()
                + ", free shipping from " + store.shipping().freeFrom() + ", delivery " + store.shipping().deliveryTime());

        section("3.3 @Value");
        print(storeInfo.describe());

        section("3.4 Profiles");
        print("active profiles " + Arrays.toString(environment.getActiveProfiles()) + ", banner: " + store.banner());

        section("3.5 spring.config.import");
        campaigns.active().forEach(campaign -> print(campaign.code() + " → %" + campaign.percent()));

        section("3.6 Our own starter");
        print(greetings.greet("Ayşe"));

        section("3.7 Conditions report");
        print("GreetingAutoConfiguration      " + conditions.explain("GreetingAutoConfiguration"));
        print("MessageSourceAutoConfiguration " + conditions.explain("MessageSourceAutoConfiguration"));
        print("DataSourceAutoConfiguration    " + conditions.explain("DataSourceAutoConfiguration"));
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
