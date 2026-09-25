package com.springbootedu.springai.exercise2;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Exercise 2 — answers customer questions with the matching FAQ entry in the prompt.
 */
@Service
public class FaqAssistant {

    private final ChatClient chat;

    FaqAssistant(ChatClient.Builder builder, @SuppressWarnings("unused") VectorStore vectorStore) {
        // TODO 2b: the best-matching FAQ entry (only one) must be part of every prompt
        this.chat = builder.build();
    }

    public String answer(String question) {
        return chat.prompt().user(question).call().content();
    }
}
