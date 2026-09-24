package com.springbootedu.graphqlwebsocket.graphql;

import com.springbootedu.graphqlwebsocket.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

/**
 * Lesson 3.2 — over HTTP, every GraphQL request is a POST to /graphql with the query in a JSON body.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "bookstore.tour.enabled=false")
@AutoConfigureHttpGraphQlTester
@Import(TestcontainersConfiguration.class)
class GraphQlOverHttpTest {

    @Autowired
    HttpGraphQlTester graphQl;

    @Test
    void theHttpEndpointAnswersQueries() {
        graphQl.document("{ book(isbn: \"9781617297571\") { title price } }")
                .execute()
                .path("book.title").entity(String.class).isEqualTo("Spring in Action")
                .path("book.price").entity(Double.class).isEqualTo(95.0);
    }
}
