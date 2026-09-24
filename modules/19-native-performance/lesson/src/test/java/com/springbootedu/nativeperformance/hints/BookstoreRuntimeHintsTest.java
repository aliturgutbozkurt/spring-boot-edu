package com.springbootedu.nativeperformance.hints;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.nativeperformance.price.EuroFormat;
import com.springbootedu.nativeperformance.price.TurkishLiraFormat;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

/**
 * Lesson 3.4 — hints are plain data: a unit test proves that the native image will contain what the code needs.
 */
class BookstoreRuntimeHintsTest {

    // tag::hints-test[]
    @Test
    void theHintsCoverTheReflectionAndTheResources() {
        RuntimeHints hints = new RuntimeHints();
        new BookstoreRuntimeHints().registerHints(hints, getClass().getClassLoader());

        assertThat(RuntimeHintsPredicates.reflection().onType(TurkishLiraFormat.class)).accepts(hints);
        assertThat(RuntimeHintsPredicates.reflection().onType(EuroFormat.class)).accepts(hints);
        assertThat(RuntimeHintsPredicates.resource().forResource("quotes/quotes.txt")).accepts(hints);
    }
    // end::hints-test[]
}
