package com.springbootedu.modulith.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.springbootedu.modulith.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;

@ApplicationModuleTest(ApplicationModuleTest.BootstrapMode.DIRECT_DEPENDENCIES)   // review + catalog
@Import(TestcontainersConfiguration.class)
class Exercise1ReviewTest {

    @Autowired
    ReviewService reviews;

    @Test
    void reviewsStillWork() {
        reviews.rate("9781617297571", 5);
        reviews.rate("9781617297571", 4);

        assertThat(reviews.averageOf("9781617297571")).isEqualTo(4.5);
        assertThatThrownBy(() -> reviews.rate("0000000000000", 3)).isInstanceOf(UnknownBookException.class);
    }
}
