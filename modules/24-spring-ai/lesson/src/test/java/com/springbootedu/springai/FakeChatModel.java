package com.springbootedu.springai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;

/**
 * Lesson 3.8 — a ChatModel for tests: no LLM, fixed answers, and every prompt is kept for assertions.
 */
// tag::fake-chat-model[]
public class FakeChatModel implements ChatModel {

    private final Deque<String> answers = new ArrayDeque<>();
    private final List<Prompt> prompts = new ArrayList<>();

    @Override
    public ChatResponse call(Prompt prompt) {
        prompts.add(prompt);
        String answer = answers.isEmpty() ? "OK" : answers.removeFirst();
        return new ChatResponse(List.of(new Generation(new AssistantMessage(answer))));
    }

    @Override
    public ChatOptions getOptions() {
        return ToolCallingChatOptions.builder().build();     // like the real models: tools can be passed along
    }

    public void willAnswer(String... texts) {
        answers.addAll(List.of(texts));
    }

    public Prompt lastPrompt() {
        return prompts.getLast();
    }
    // end::fake-chat-model[]

    public void reset() {
        answers.clear();
        prompts.clear();
    }
}
