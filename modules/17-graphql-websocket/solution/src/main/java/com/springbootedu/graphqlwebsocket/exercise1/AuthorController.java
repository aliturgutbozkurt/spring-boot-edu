package com.springbootedu.graphqlwebsocket.exercise1;

import com.springbootedu.graphqlwebsocket.catalog.Author;
import com.springbootedu.graphqlwebsocket.catalog.Book;
import com.springbootedu.graphqlwebsocket.catalog.Catalog;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * Exercise 1 — Query.authors, Query.author and Author.books.
 */
@Controller
class AuthorController {

    private final Catalog catalog;

    AuthorController(Catalog catalog) {
        this.catalog = catalog;
    }

    @QueryMapping
    List<Author> authors() {
        return catalog.findAllAuthors();
    }

    @QueryMapping
    @Nullable Author author(@Argument long id) {
        return catalog.findAuthor(id).orElse(null);
    }

    @BatchMapping
    Map<Author, List<Book>> books(List<Author> authors) {
        List<Long> ids = authors.stream().map(Author::id).toList();
        Map<Long, List<Book>> booksByAuthor = catalog.findBooksOf(ids).stream()
                .collect(Collectors.groupingBy(Book::authorId));
        return authors.stream().collect(Collectors.toMap(
                author -> author,
                author -> booksByAuthor.getOrDefault(author.id(), List.of())));   // an author without books: []
    }
}
