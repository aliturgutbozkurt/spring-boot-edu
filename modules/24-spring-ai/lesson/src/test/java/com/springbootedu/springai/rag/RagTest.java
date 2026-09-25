package com.springbootedu.springai.rag;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.springai.AiTest;
import com.springbootedu.springai.FakeChatModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * Lesson 3.6 — the ETL pipeline fills pgvector; the advisor puts the matching description into the prompt.
 */
@AiTest
class RagTest {

    @Autowired
    BookCatalogIngestion ingestion;

    @Autowired
    BookQuestions questions;

    @Autowired
    VectorStore vectorStore;

    @Autowired
    FakeChatModel model;

    @Autowired
    JdbcClient jdbc;

    @BeforeEach
    void ingest() {
        model.reset();
        assertThat(ingestion.ingest()).isEqualTo(4);            // one chunk per short description
    }

    @Test
    void theVectorSearchFindsTheMatchingBook() {
        var results = vectorStore.similaritySearch(SearchRequest.builder()
                .query("a book about distributed database replication").topK(1).build());

        assertThat(results).extracting(Document::getMetadata).first()
                .extracting(metadata -> metadata.get("isbn")).isEqualTo("9781449373320");
    }

    // tag::rag-test[]
    @Test
    void theRetrievedDescriptionIsPartOfThePrompt() {
        questions.answer("Which book explains distributed database replication?");

        assertThat(model.lastPrompt().getContents())
                .contains("Martin Kleppmann")                      // retrieved from pgvector
                .doesNotContain("Head First");                     // not similar enough
    }
    // end::rag-test[]

    @Test
    void ingestingTwiceReplacesTheDocuments() {
        ingestion.ingest();

        Integer rows = jdbc.sql("SELECT count(*) FROM springai.vector_store").query(Integer.class).single();
        assertThat(rows).isEqualTo(4);                           // not 8: the old documents were deleted
    }
}
