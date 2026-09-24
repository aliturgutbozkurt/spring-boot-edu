package com.springbootedu.reactive;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Exercise1Test {

    @Test
    void answersTheQuestion() {
        assertThat(new Exercise1().answer()).isEqualTo("42");
    }
}
