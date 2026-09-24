package com.springbootedu.security;

import java.util.Map;
import java.util.function.IntSupplier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * Runs the lesson's examples once against the running server, as an HTTP client would.
 * Start it with: ./mvnw -pl modules/12-security/lesson spring-boot:run   (Docker must be running)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final Environment environment;

    LessonTour(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        String port = environment.getProperty("local.server.port");
        if (port == null) {
            return;                                                     // no real server (e.g. a MockMvc test)
        }
        RestClient http = RestClient.create("http://localhost:" + port);

        section("3.3 Public and protected");
        print("GET /api/books (anonymous): " + http.get().uri("/api/books").retrieve().body(String.class));
        print("POST /api/books as ada (CUSTOMER): " + status(() -> http.post().uri("/api/books")
                .headers(h -> h.setBasicAuth("ada", "ada-password"))
                .contentType(MediaType.APPLICATION_JSON).body(Map.of("isbn", "1", "title", "x"))
                .retrieve().toBodilessEntity().getStatusCode().value()));

        section("3.6 A token from our Authorization Server");
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "client_credentials");
        form.add("scope", "books.read books.write");
        Map<?, ?> token = http.post().uri("/oauth2/token")
                .headers(h -> h.setBasicAuth("bookstore-cli", "dev-only-secret"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form)
                .retrieve().body(Map.class);
        String accessToken = String.valueOf(token == null ? null : token.get("access_token"));
        print("access_token: " + accessToken.substring(0, Math.min(40, accessToken.length())) + "…");

        section("3.5 Calling the API with the JWT");
        print("GET /api/me: " + http.get().uri("/api/me")
                .headers(h -> h.setBearerAuth(accessToken)).retrieve().body(String.class));

        section("3.1 In the browser");
        print("open http://localhost:" + port + "/account and log in as ada / ada-password (Ctrl+C stops the app)");
    }

    private static String status(IntSupplier call) {
        try {
            return String.valueOf(call.getAsInt());
        } catch (HttpClientErrorException e) {
            return e.getStatusCode().value() + " " + e.getStatusText();
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("== " + title);
    }

    private static void print(String line) {
        System.out.println(line);
    }
}
