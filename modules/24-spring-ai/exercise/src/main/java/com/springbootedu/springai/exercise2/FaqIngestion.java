package com.springbootedu.springai.exercise2;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * Exercise 2 — loads the FAQ texts (classpath:faq/*.md) into the vector store.
 */
@Component
public class FaqIngestion {

    private final ResourcePatternResolver resources;
    private final VectorStore vectorStore;

    FaqIngestion(ResourcePatternResolver resources, VectorStore vectorStore) {
        this.resources = resources;
        this.vectorStore = vectorStore;
    }

    public int ingest() {
        // TODO 2a: read every classpath:faq/*.md file as a Document (metadata "collection" = "faq"),
        //          add all of them to the vector store and return how many were added.
        //          Ingesting twice must not create duplicates.
        throw new UnsupportedOperationException("TODO 2a — " + resources + vectorStore);
    }
}
