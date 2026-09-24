package com.springbootedu.graphqlwebsocket.graphql;

import com.springbootedu.graphqlwebsocket.TestcontainersConfiguration;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;

/**
 * Lesson 3.4 — a mutation with an input type, and errors that reach the client with a useful message.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@AutoConfigureGraphQlTester
@Import(TestcontainersConfiguration.class)
class ReviewMutationTest {

    private static final String ADD_REVIEW = """
            mutation($input: ReviewInput!) {
              addReview(input: $input) { id stars text }
            }""";

    @Autowired
    GraphQlTester graphQl;

    @Test
    void aReviewIsAdded() {
        graphQl.document(ADD_REVIEW)
                .variable("input", Map.of("isbn", "9780321336781", "stars", 5, "text", "Fun and scary."))
                .execute()
                .path("addReview.id").hasValue()
                .path("addReview.text").entity(String.class).isEqualTo("Fun and scary.");
    }

    @Test
    void aWrongNumberOfStarsIsABadRequest() {
        graphQl.document(ADD_REVIEW)
                .variable("input", Map.of("isbn", "9780321336781", "stars", 6, "text", "Too good."))
                .execute()
                .errors().expect(error -> error.getErrorType() == ErrorType.BAD_REQUEST
                        && error.getMessage().contains("between 1 and 5"))
                .verify();
    }

    @Test
    void anUnknownBookIsNotFound() {
        graphQl.document(ADD_REVIEW)
                .variable("input", Map.of("isbn", "0000000000000", "stars", 3, "text", "?"))
                .execute()
                .errors().expect(error -> error.getErrorType() == ErrorType.NOT_FOUND)
                .verify();
    }
}
