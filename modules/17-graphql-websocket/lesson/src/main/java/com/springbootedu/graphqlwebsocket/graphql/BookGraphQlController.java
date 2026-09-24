package com.springbootedu.graphqlwebsocket.graphql;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

/**
 * Lessons 3.2–3.5 — one method per schema field. The method name is the field name.
 */
@Controller
class BookGraphQlController {

    private final CatalogRepository catalog;
    private final ReviewService reviews;

    BookGraphQlController(CatalogRepository catalog, ReviewService reviews) {
        this.catalog = catalog;
        this.reviews = reviews;
    }

    // tag::queries[]
    @QueryMapping                                     // Query.books
    List<Book> books() {
        return catalog.findAllBooks();
    }

    @QueryMapping                                     // Query.book(isbn: ID!) — null when unknown
    @Nullable Book book(@Argument String isbn) {
        return catalog.findBook(isbn).orElse(null);
    }
    // end::queries[]

    // tag::batch-mapping[]
    /** Book.author for ALL books of the result in one call: no N+1. */
    @BatchMapping
    Map<Book, Author> author(List<Book> books) {
        Set<Long> ids = books.stream().map(Book::authorId).collect(Collectors.toSet());
        Map<Long, Author> authors = catalog.findAuthors(ids).stream()
                .collect(Collectors.toMap(Author::id, Function.identity()));
        return books.stream().collect(Collectors.toMap(Function.identity(), book -> authors.get(book.authorId())));
    }
    // end::batch-mapping[]

    // tag::schema-mapping[]
    /** Book.reviews, called once PER book: simple, but N+1 queries for a list of N books. */
    @SchemaMapping
    List<Review> reviews(Book book) {
        return catalog.findReviews(book.isbn());
    }
    // end::schema-mapping[]

    // tag::mutation[]
    @MutationMapping
    Review addReview(@Argument ReviewInput input) {
        return reviews.add(input);
    }

    @GraphQlExceptionHandler                           // like @ExceptionHandler, but produces a GraphQL error
    GraphQLError invalidReview(InvalidReviewException e, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env).errorType(ErrorType.BAD_REQUEST).message(e.getMessage()).build();
    }

    @GraphQlExceptionHandler
    GraphQLError bookNotFound(BookNotFoundException e, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env).errorType(ErrorType.NOT_FOUND).message(e.getMessage()).build();
    }
    // end::mutation[]

    // tag::subscription[]
    @SubscriptionMapping
    Flux<Review> reviewAdded(@Argument String isbn) {
        return reviews.reviewsAdded().filter(review -> review.isbn().equals(isbn));
    }
    // end::subscription[]
}
