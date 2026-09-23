package com.springbootedu.corecontainer.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.corecontainer.book.InMemoryBookCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Lesson 3.5 — one of two implementations is chosen by a property.
 */
class RecommendationConditionTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withBean(InMemoryBookCatalog.class)
            .withUserConfiguration(PopularBooksRecommendation.class, BudgetBooksRecommendation.class);

    @Test
    void popularIsTheDefaultWhenThePropertyIsMissing() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(RecommendationService.class);
            assertThat(context.getBean(RecommendationService.class)).isInstanceOf(PopularBooksRecommendation.class);
        });
    }

    @Test
    void budgetIsChosenWithTheProperty() {
        runner.withPropertyValues("bookstore.recommendations.strategy=budget")
                .run(context -> assertThat(context.getBean(RecommendationService.class).recommend())
                        .first()
                        .extracting("title")
                        .isEqualTo("Java Puzzlers"));
    }

    @Test
    void anUnknownValueLeavesNoImplementation() {
        runner.withPropertyValues("bookstore.recommendations.strategy=unknown")
                .run(context -> assertThat(context).doesNotHaveBean(RecommendationService.class));
    }
}
