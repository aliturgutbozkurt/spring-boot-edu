package com.springbootedu.setupmodernjava.collections;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Lesson 3.7 — sequenced collections.
 */
class RecentlyViewedTest {

    @Test
    void keepsTheMostRecentFirstWithoutDuplicatesAndLimited() {
        var recent = new RecentlyViewed(3);
        recent.view("A");
        recent.view("B");
        recent.view("C");
        recent.view("A");      // moves to the front
        recent.view("D");      // pushes out the oldest ("B")

        assertThat(recent.newestFirst()).containsExactly("D", "A", "C");
        assertThat(recent.oldestFirst()).containsExactly("C", "A", "D");
        assertThat(recent.latest()).contains("D");
    }

    @Test
    void emptyHistoryHasNoLatest() {
        assertThat(new RecentlyViewed(3).latest()).isEmpty();
    }
}
