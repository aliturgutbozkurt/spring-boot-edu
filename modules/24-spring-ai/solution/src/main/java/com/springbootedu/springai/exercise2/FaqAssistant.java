package com.springbootedu.springai.exercise2;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Exercise 2 — answers customer questions with the matching FAQ entry in the prompt.
 */
@Service
public class FaqAssistant {

    private final ChatClient chat;

    FaqAssistant(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chat = builder
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder().topK(1).build())
                        .build())
                .build();
    }

    public String answer(String question) {
        return chat.prompt().user(question).call().content();
    }
}
