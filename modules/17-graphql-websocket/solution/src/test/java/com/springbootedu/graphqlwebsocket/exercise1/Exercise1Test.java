package com.springbootedu.graphqlwebsocket.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.graphqlwebsocket.catalog.Catalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@SpringBootTest
@AutoConfigureGraphQlTester
class Exercise1Test {

    @Autowired
    GraphQlTester graphQl;

    @Autowired
    Catalog catalog;

    @Test
    void authorsComeWithTheirBooks() {
        graphQl.document("{ authors { name books { title } } }")
                .execute()
                .path("authors[*].name").entityList(String.class)
                .containsExactly("Joshua Bloch", "Craig Walls", "Martin Kleppmann")
                .path("authors[0].books[*].title").entityList(String.class)
                .containsExactly("Effective Java", "Java Puzzlers");
    }

    @Test
    void oneAuthorByIdAndAnUnknownOneIsNull() {
        graphQl.document("{ author(id: 3) { name books { title price } } }")
                .execute()
                .path("author.name").entity(String.class).isEqualTo("Martin Kleppmann")
                .path("author.books[0].price").entity(Double.class).isEqualTo(110.0);

        graphQl.document("{ author(id: 99) { name } }")
                .execute()
                .path("author").valueIsNull();
    }

    @Test
    void theBooksOfAllAuthorsAreLoadedInOneCall() {
        catalog.resetCounters();

        graphQl.document("{ authors { books { title } } }").execute()
                .path("authors").entityList(Object.class).hasSize(3);

        assertThat(catalog.bookQueries()).isEqualTo(1);
    }
}
