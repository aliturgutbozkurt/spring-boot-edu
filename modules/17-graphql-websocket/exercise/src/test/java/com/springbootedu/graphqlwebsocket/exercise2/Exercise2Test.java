package com.springbootedu.graphqlwebsocket.exercise2;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;

@SpringBootTest
@AutoConfigureGraphQlTester
class Exercise2Test {

    private static final String PLACE_ORDER = """
            mutation($input: OrderInput!) {
              placeOrder(input: $input) { id customerId total lines { quantity book { title } } }
            }""";

    @Autowired
    GraphQlTester graphQl;

    @Test
    void anOrderIsPlaced() {
        graphQl.document(PLACE_ORDER)
                .variable("input", Map.of("customerId", "42", "lines", List.of(
                        Map.of("isbn", "9780134685991", "quantity", 2),
                        Map.of("isbn", "9781617297571", "quantity", 1))))
                .execute()
                .path("placeOrder.id").hasValue()
                .path("placeOrder.customerId").entity(String.class).isEqualTo("42")
                .path("placeOrder.total").entity(Double.class).isEqualTo(274.8)
                .path("placeOrder.lines[*].book.title").entityList(String.class)
                .containsExactly("Effective Java", "Spring in Action");
    }

    @Test
    void anEmptyOrderIsABadRequest() {
        graphQl.document(PLACE_ORDER)
                .variable("input", Map.of("customerId", "42", "lines", List.of()))
                .execute()
                .errors().expect(error -> error.getErrorType() == ErrorType.BAD_REQUEST)
                .verify();
    }

    @Test
    void anUnknownBookIsNotFound() {
        graphQl.document(PLACE_ORDER)
                .variable("input", Map.of("customerId", "42", "lines", List.of(
                        Map.of("isbn", "0000000000000", "quantity", 1))))
                .execute()
                .errors().expect(error -> error.getErrorType() == ErrorType.NOT_FOUND)
                .verify();
    }

    @Test
    void tooManyCopiesAreABadRequestWithTheReason() {
        graphQl.document(PLACE_ORDER)
                .variable("input", Map.of("customerId", "42", "lines", List.of(
                        Map.of("isbn", "9781617297571", "quantity", 99))))
                .execute()
                .errors().expect(error -> error.getErrorType() == ErrorType.BAD_REQUEST
                        && error.getMessage().contains("Not enough stock"))
                .verify();
    }
}
