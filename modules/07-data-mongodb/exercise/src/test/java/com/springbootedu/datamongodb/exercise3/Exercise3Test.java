package com.springbootedu.datamongodb.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.datamongodb.TestcontainersConfiguration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Exercise 3 — full-text search where a match in the title counts more than one in the body.
 */
@DataMongoTest
@Import({TestcontainersConfiguration.class, ArticleSearch.class})
class Exercise3Test {

    @Autowired
    ArticleSearch search;

    @Autowired
    MongoTemplate mongo;

    @BeforeEach
    void seed() {
        mongo.remove(new org.springframework.data.mongodb.core.query.Query(), Article.class);
        mongo.insertAll(List.of(
                new Article(null, "Choosing the right index", "Compound keys and their order."),
                new Article(null, "Schema design", "Embed or reference? Also think about which index you need."),
                new Article(null, "Transactions", "Replica sets make multi-document transactions possible."),
                new Article(null, "Aggregations", "Pipelines group and sort documents.")));
    }

    @Test
    void findsArticlesContainingAnyOfTheWords() {
        assertThat(search.search("transactions pipelines", 10)).extracting(Article::title)
                .containsExactlyInAnyOrder("Transactions", "Aggregations");
    }

    @Test
    void aTitleMatchRanksAboveABodyMatch() {
        assertThat(search.search("index", 10)).extracting(Article::title)
                .containsExactly("Choosing the right index", "Schema design");
    }

    @Test
    void stemmingMatchesOtherWordForms() {
        assertThat(search.search("indexes", 10)).hasSize(2);           // "indexes" → "index"
    }

    @Test
    void theLimitIsRespected() {
        assertThat(search.search("index", 1)).extracting(Article::title).containsExactly("Choosing the right index");
    }
}
