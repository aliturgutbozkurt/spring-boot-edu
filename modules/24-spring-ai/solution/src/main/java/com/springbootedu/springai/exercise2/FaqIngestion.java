package com.springbootedu.springai.exercise2;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
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
        vectorStore.delete("collection == 'faq'");
        List<Document> documents = new ArrayList<>();
        for (Resource file : faqFiles()) {
            TextReader reader = new TextReader(file);
            reader.getCustomMetadata().put("collection", "faq");
            documents.addAll(reader.get());
        }
        vectorStore.add(documents);                     // the texts are short: no splitting needed
        return documents.size();
    }

    private Resource[] faqFiles() {
        try {
            return resources.getResources("classpath:faq/*.md");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
