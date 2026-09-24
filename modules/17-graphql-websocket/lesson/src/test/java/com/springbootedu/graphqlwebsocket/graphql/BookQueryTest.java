package com.springbootedu.graphqlwebsocket.graphql;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.graphqlwebsocket.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;

/**
 * Lessons 3.2–3.3 — queries, nested fields, and the N+1 problem solved with @BatchMapping.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@AutoConfigureGraphQlTester                      // executes queries without HTTP
@Import(TestcontainersConfiguration.class)
class BookQueryTest {

    @Autowired
    GraphQlTester graphQl;

    @Autowired
    CatalogRepository catalog;

    @Test
    void theClientChoosesTheFields() {
        graphQl.document("{ books { title } }")
                .execute()
                .path("books[*].title").entityList(String.class)
                .contains("Effective Java", "Spring in Action")
                .hasSizeGreaterThan(3);
    }

    @Test
    void oneQueryFollowsTheRelations() {
        graphQl.document("""
                        query($isbn: ID!) {
                          book(isbn: $isbn) { title author { name } reviews { stars } }
                        }""")
                .variable("isbn", "9780134685991")
                .execute()
                .path("book.author.name").entity(String.class).isEqualTo("Joshua Bloch")
                .path("book.reviews[*].stars").entityList(Integer.class).contains(5, 4);
    }

    @Test
    void anUnknownBookIsNull() {
        graphQl.document("{ book(isbn: \"0000000000000\") { title } }")
                .execute()
                .path("book").valueIsNull();
    }

    @Test
    void batchMappingLoadsAllAuthorsInOneCallButReviewsNeedOneCallPerBook() {
        catalog.resetCounters();

        graphQl.document("{ books { title author { name } reviews { stars } } }").execute()
                .path("books").entityList(Object.class).hasSizeGreaterThan(3);

        assertThat(catalog.authorQueries()).isEqualTo(1);                          // @BatchMapping
        assertThat(catalog.reviewQueries()).isGreaterThanOrEqualTo(4);              // @SchemaMapping: N+1
    }
}
