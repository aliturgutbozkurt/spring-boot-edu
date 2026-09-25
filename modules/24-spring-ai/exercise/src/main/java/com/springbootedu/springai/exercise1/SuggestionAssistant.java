package com.springbootedu.springai.exercise1;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Exercise 1 — book suggestions as a list of records.
 */
@Service
public class SuggestionAssistant {

    private final ChatClient chat;
    private final Resource template;

    SuggestionAssistant(ChatClient.Builder builder, @Value("classpath:prompts/suggest.st") Resource template) {
        this.chat = builder.build();
        this.template = template;
    }

    public List<BookSuggestion> suggest(String interests, int count) {
        // TODO 1a: the user message comes from the template (prompts/suggest.st) with "interests" and "count"
        // TODO 1b: the answer is converted into a List<BookSuggestion>
        throw new UnsupportedOperationException("TODO 1 — " + chat + template + interests + count);
    }
}
