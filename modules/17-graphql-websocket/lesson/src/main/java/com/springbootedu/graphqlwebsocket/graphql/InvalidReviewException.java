package com.springbootedu.graphqlwebsocket.graphql;

/**
 * Lesson 3.4 — becomes a GraphQL error of type BAD_REQUEST (see {@link BookGraphQlController}).
 */
public class InvalidReviewException extends RuntimeException {

    public InvalidReviewException(String message) {
        super(message);
    }
}
