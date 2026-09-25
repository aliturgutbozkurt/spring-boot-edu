package com.springbootedu.springai.exercise3;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Exercise 3 — the customer support assistant, which may look up orders.
 */
@Service
public class SupportAssistant {

    private final ChatClient chat;

    SupportAssistant(ChatClient.Builder builder, @SuppressWarnings("unused") OrderTools orderTools) {
        this.chat = builder
                .defaultSystem("You are the customer support of an online bookstore. Be short and friendly.")
                // TODO 3b: the model may call the tools of OrderTools
                .build();
    }

    public String help(String message) {
        return chat.prompt().user(message).call().content();
    }
}
