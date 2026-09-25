package com.springbootedu.springai;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

/**
 * Given — an EmbeddingModel for tests: one dimension per keyword. Texts that share keywords get similar
 * vectors, so a vector search behaves predictably — enough to test retrieval without a real model.
 */
public class KeywordEmbeddingModel implements EmbeddingModel {

    public static final List<String> KEYWORDS = List.of(
            "shipping", "shipped", "delivery", "cargo", "return", "refund", "payment", "card",
            "instalment", "days", "free", "transfer", "book", "order", "java", "design");

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        List<Embedding> embeddings = new ArrayList<>();
        for (int i = 0; i < request.getInstructions().size(); i++) {
            embeddings.add(new Embedding(vector(request.getInstructions().get(i)), i));
        }
        return new EmbeddingResponse(embeddings);
    }

    @Override
    public float[] embed(Document document) {
        return vector(document.getText());
    }

    @Override
    public int dimensions() {
        return KEYWORDS.size();
    }

    private static float[] vector(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        float[] vector = new float[KEYWORDS.size()];
        double length = 0;
        for (int i = 0; i < KEYWORDS.size(); i++) {
            vector[i] = lower.split(KEYWORDS.get(i), -1).length - 1;       // how often the keyword occurs
            length += vector[i] * vector[i];
        }
        for (int i = 0; i < vector.length && length > 0; i++) {
            vector[i] = (float) (vector[i] / Math.sqrt(length));
        }
        if (length == 0) {
            vector[0] = 1e-3f;                                               // pgvector cannot compare zero vectors
        }
        return vector;
    }
}
