package com.springbootedu.springai.exercise2;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.springai.AiTest;
import com.springbootedu.springai.FakeChatModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;

@AiTest
class Exercise2Test {

    @Autowired
    FaqIngestion ingestion;

    @Autowired
    FaqAssistant assistant;

    @Autowired
    FakeChatModel model;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    void ingest() {
        model.reset();
        assertThat(ingestion.ingest()).isEqualTo(3);
    }

    @Test
    void theMatchingFaqEntryIsInThePrompt() {
        assistant.answer("How many days do I have to return a book and get a refund?");

        assertThat(model.lastPrompt().getContents())
                .contains("within 14 days")
                .doesNotContain("instalments");
    }

    @Test
    void anotherQuestionFindsAnotherEntry() {
        assistant.answer("Can I pay by card in instalments?");

        assertThat(model.lastPrompt().getContents()).contains("3 instalments");
    }

    @Test
    void ingestingAgainDoesNotDuplicate() {
        ingestion.ingest();

        assertThat(jdbc.sql("SELECT count(*) FROM vector_store").query(Integer.class).single()).isEqualTo(3);
    }
}
