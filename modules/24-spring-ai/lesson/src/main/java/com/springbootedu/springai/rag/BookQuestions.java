package com.springbootedu.springai.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Lesson 3.6 — RAG: the advisor searches the vector store and puts the best matches into the prompt.
 */
// tag::rag[]
@Service
public class BookQuestions {

    private final ChatClient chat;

    BookQuestions(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chat = builder
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder().topK(2).similarityThreshold(0.3).build())
                        .build())
                .build();
    }

    public String answer(String question) {
        return chat.prompt().user(question).call().content();
    }
}
// end::rag[]
