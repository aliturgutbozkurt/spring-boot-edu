package com.springbootedu.datajdbcpostgres.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.datajdbcpostgres.TestcontainersConfiguration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;

/**
 * Exercise 2 — a JdbcClient repository with a batch insert. Uses the table of exercise 1.
 */
@JdbcTest
@Import({TestcontainersConfiguration.class, ReviewRepository.class})
class Exercise2Test {

    @Autowired
    ReviewRepository reviews;

    @Test
    void addsAReviewAndReturnsItsId() {
        long id = reviews.add(new NewReview("9780134685991", 5, "Harika"));

        assertThat(id).isPositive();
        assertThat(reviews.findByIsbn("9780134685991"))
                .singleElement()
                .satisfies(review -> {
                    assertThat(review.id()).isEqualTo(id);
                    assertThat(review.stars()).isEqualTo(5);
                    assertThat(review.comment()).isEqualTo("Harika");
                });
    }

    @Test
    void averageStarsOfABook() {
        reviews.add(new NewReview("9780134685991", 5, "a"));
        reviews.add(new NewReview("9780134685991", 2, "b"));

        assertThat(reviews.averageStars("9780134685991")).hasValue(3.5);
        assertThat(reviews.averageStars("9780134757599")).isEmpty();          // no reviews yet
    }

    @Test
    void importsManyReviewsInOneBatch() {
        int[] counts = reviews.addAll(List.of(
                new NewReview("9780134757599", 4, "x"),
                new NewReview("9780134757599", 5, "y"),
                new NewReview("9780134685991", 3, "z")));

        assertThat(counts).containsExactly(1, 1, 1);
        assertThat(reviews.findByIsbn("9780134757599")).hasSize(2);
    }
}
