package com.springbootedu.springai.rag;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.6 — the ETL pipeline of RAG: read the book descriptions, split them, embed and store them.
 */
@Component
public class BookCatalogIngestion {

    private final ResourcePatternResolver resources;
    private final VectorStore vectorStore;

    BookCatalogIngestion(ResourcePatternResolver resources, VectorStore vectorStore) {
        this.resources = resources;
        this.vectorStore = vectorStore;
    }

    // tag::etl[]
    public int ingest() {
        vectorStore.delete("collection == 'book-descriptions'");        // idempotent: replace the old documents
        List<Document> documents = new ArrayList<>();
        for (Resource file : descriptionFiles()) {                      // Extract
            TextReader reader = new TextReader(file);
            reader.getCustomMetadata().put("collection", "book-descriptions");   // "source" is the file name
            reader.getCustomMetadata().put("isbn", file.getFilename().replace(".md", ""));
            documents.addAll(reader.get());
        }
        List<Document> chunks = TokenTextSplitter.builder().build().apply(documents);   // Transform
        vectorStore.add(chunks);                                        // Load: the EmbeddingModel computes vectors
        return chunks.size();
    }
    // end::etl[]

    private Resource[] descriptionFiles() {
        try {
            return resources.getResources("classpath:books/*.md");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
