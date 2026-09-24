package com.springbootedu.graphqlwebsocket.exercise2;

import com.springbootedu.graphqlwebsocket.catalog.Book;
import com.springbootedu.graphqlwebsocket.catalog.Catalog;
import com.springbootedu.graphqlwebsocket.catalog.OutOfStockException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import java.math.BigDecimal;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;

/**
 * Exercise 2 — Mutation.placeOrder, Order.total, OrderLine.book and the errors.
 */
@Controller
class OrderController {

    private final OrderService orders;
    private final Catalog catalog;

    OrderController(OrderService orders, Catalog catalog) {
        this.orders = orders;
        this.catalog = catalog;
    }

    @MutationMapping
    PlacedOrder placeOrder(@Argument OrderInput input) {
        return orders.place(input);
    }

    @SchemaMapping(typeName = "Order")
    BigDecimal total(PlacedOrder order) {
        return order.lines().stream()
                .map(line -> line.unitPrice().multiply(BigDecimal.valueOf(line.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @SchemaMapping(typeName = "OrderLine")
    Book book(PlacedLine line) {
        return catalog.findBook(line.isbn()).orElseThrow(() -> new UnknownBookException(line.isbn()));
    }

    @GraphQlExceptionHandler({InvalidOrderException.class, OutOfStockException.class})
    GraphQLError badRequest(RuntimeException e, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env).errorType(ErrorType.BAD_REQUEST).message(e.getMessage()).build();
    }

    @GraphQlExceptionHandler
    GraphQLError notFound(UnknownBookException e, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env).errorType(ErrorType.NOT_FOUND).message(e.getMessage()).build();
    }
}
