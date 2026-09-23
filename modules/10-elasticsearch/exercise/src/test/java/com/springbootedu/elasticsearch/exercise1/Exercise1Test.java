package com.springbootedu.elasticsearch.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.elasticsearch.TestcontainersConfiguration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.elasticsearch.test.autoconfigure.DataElasticsearchTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

/**
 * Exercise 1 — suggest book titles while the user is still typing.
 */
@DataElasticsearchTest
@Import({TestcontainersConfiguration.class, TitleSuggestions.class})
class Exercise1Test {

    @Autowired
    TitleSuggestions suggestions;

    @Autowired
    ElasticsearchOperations operations;

    @BeforeEach
    void seed() {
        var index = operations.indexOps(TitleDocument.class);
        index.delete();
        index.createWithMapping();                                     // uses the mapping of TitleDocument
        operations.save(List.of(
                new TitleDocument("1", "Effective Java"),
                new TitleDocument("2", "Java Puzzlers"),
                new TitleDocument("3", "Spring in Action"),
                new TitleDocument("4", "Spring Security in Action"),
                new TitleDocument("5", "Clean Architecture")));
        index.refresh();
    }

    @Test
    void theTitleFieldIsSearchAsYouType() {
        var mapping = operations.indexOps(TitleDocument.class).getMapping();

        assertThat(typeOf(mapping, "title")).isEqualTo("search_as_you_type");
    }

    @Test
    void aPrefixOfTheFirstWordIsEnough() {
        assertThat(suggestions.suggest("spr", 10))
                .containsExactlyInAnyOrder("Spring in Action", "Spring Security in Action");
    }

    @Test
    void theLastWordMayBeIncomplete() {
        assertThat(suggestions.suggest("java puz", 10)).first().isEqualTo("Java Puzzlers");
    }

    @Test
    void anyWordOfTheTitleCanMatch() {
        assertThat(suggestions.suggest("arch", 10)).containsExactly("Clean Architecture");
    }

    @Test
    void theNumberOfSuggestionsIsLimited() {
        assertThat(suggestions.suggest("j", 1)).hasSize(1);
    }

    @SuppressWarnings("unchecked")
    private static Object typeOf(Map<String, Object> mapping, String field) {
        var properties = (Map<String, Object>) mapping.get("properties");
        return ((Map<String, Object>) properties.get(field)).get("type");
    }
}
