package com.springbootedu.graphqlwebsocket;

import com.springbootedu.graphqlwebsocket.graphql.CatalogRepository;
import com.springbootedu.graphqlwebsocket.graphql.ReviewInput;
import com.springbootedu.graphqlwebsocket.graphql.ReviewService;
import java.util.List;
import java.util.Map;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.core.env.Environment;
import org.springframework.graphql.client.FieldAccessException;
import org.springframework.graphql.client.HttpSyncGraphQlClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import reactor.core.Disposable;

/**
 * Runs the lesson's examples once (section 3) against the running server.
 * Start it with: ./mvnw -pl modules/17-graphql-websocket/lesson spring-boot:run   (Docker must be running)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final Environment environment;
    private final CatalogRepository catalog;
    private final ReviewService reviews;

    LessonTour(Environment environment, CatalogRepository catalog, ReviewService reviews) {
        this.environment = environment;
        this.catalog = catalog;
        this.reviews = reviews;
    }

    @Override
    public void run(ApplicationArguments args) {
        // the port is known only after the web server has started (it can be random)
        String baseUrl = "http://localhost:" + environment.getRequiredProperty("local.server.port");
        RestClient rest = RestClient.create(baseUrl);
        HttpSyncGraphQlClient graphQl = HttpSyncGraphQlClient.create(RestClient.create(baseUrl + "/graphql"));

        section("3.2 A query: only the fields we ask for");
        List<BookTitle> books = graphQl.document("{ books { title } }").retrieveSync("books").toEntityList(BookTitle.class);
        print("titles: " + books.stream().map(BookTitle::title).toList());

        section("3.3 N+1: author via @BatchMapping, reviews via @SchemaMapping");
        catalog.resetCounters();
        graphQl.document("{ books { title author { name } reviews { stars } } }").executeSync();
        print(books.size() + " books → author queries: " + catalog.authorQueries()
              + ", review queries: " + catalog.reviewQueries());

        section("3.5 Subscribe to new reviews of Java Puzzlers");
        Disposable subscription = reviews.reviewsAdded()
                .filter(review -> review.isbn().equals("9780321336781"))
                .subscribe(review -> print("pushed to the subscriber: " + review));

        section("3.4 A mutation, then one with a wrong number of stars");
        String addReview = "mutation($input: ReviewInput!) { addReview(input: $input) { id stars text } }";
        Map<?, ?> review = graphQl.document(addReview)
                .variable("input", Map.of("isbn", "9780321336781", "stars", 4, "text", "Tricky!"))
                .retrieveSync("addReview").toEntity(Map.class);
        print("added: " + review);
        try {
            graphQl.document(addReview)
                    .variable("input", Map.of("isbn", "9780321336781", "stars", 9, "text", "Too good."))
                    .retrieveSync("addReview").toEntity(Map.class);
        } catch (FieldAccessException e) {
            print("error: " + e.getResponse().getErrors());
        }
        reviews.add(new ReviewInput("9780321336781", 5, "Added from Java code."));
        subscription.dispose();

        section("3.6 A price change is pushed over STOMP to /topic/prices");
        rest.put().uri("/api/books/9780321336781/price").contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("price", 52.50)).retrieve().toBodilessEntity();
        print("price changed; open " + baseUrl + "/prices.html in a browser to see the next change live");
        print("GraphiQL: " + baseUrl + "/graphiql   (Ctrl+C stops the app)");
    }

    /** The client maps the JSON of each book to this record: only the requested field exists. */
    private record BookTitle(String title) {
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    private static void print(Object line) {
        System.out.println("  " + line);
    }
}
