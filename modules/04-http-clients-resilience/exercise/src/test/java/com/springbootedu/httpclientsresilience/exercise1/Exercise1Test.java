package com.springbootedu.httpclientsresilience.exercise1;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.httpclientsresilience.PartnerServerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Exercise 1 — an HTTP interface client for the review service.
 */
class Exercise1Test extends PartnerServerTest {

    @Autowired
    ReviewApi reviews;

    @Test
    void readsTheReviewsOfABook() {
        partner.stubFor(get("/reviews/9780134685991").willReturn(okJson("""
                [{"author": "ayse", "stars": 5, "text": "Harika"}, {"author": "mehmet", "stars": 4, "text": "İyi"}]""")));

        assertThat(reviews.forBook("9780134685991"))
                .extracting(Review::stars)
                .containsExactly(5, 4);
    }

    @Test
    void postsANewReviewAsJson() {
        partner.stubFor(post("/reviews/9780134685991").willReturn(aResponse().withStatus(201)));

        reviews.add("9780134685991", new Review("zeynep", 3, "Fena değil"));

        partner.verify(postRequestedFor(urlEqualTo("/reviews/9780134685991"))
                .withRequestBody(equalToJson("""
                        {"author": "zeynep", "stars": 3, "text": "Fena değil"}""")));
    }
}
