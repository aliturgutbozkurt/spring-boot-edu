package com.springbootedu.springai.assistant;

import com.springbootedu.springai.stock.StockTools;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Lessons 3.2–3.5 — one ChatClient with a system prompt, memory and a tool.
 */
@Service
public class BookAssistant {

    private final ChatClient chat;
    private final StockTools stock;
    private final Resource recommendTemplate;

    // tag::chat-client[]
    BookAssistant(ChatClient.Builder builder, ChatMemory memory, StockTools stock,
                  @Value("classpath:prompts/system.st") Resource systemPrompt,
                  @Value("classpath:prompts/recommend.st") Resource recommendTemplate) {
        this.chat = builder
                .defaultSystem(systemPrompt)                                   // rules for every conversation
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())   // earlier messages go along
                .defaultTools(stock)                                           // the model may call stockOf(...)
                .build();
        this.stock = stock;
        this.recommendTemplate = recommendTemplate;
    }

    public String ask(String conversationId, String question) {
        return chat.prompt()
                .user(question)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
    // end::chat-client[]

    // tag::structured-output[]
    public Recommendation recommend(String mood) {
        return chat.prompt()
                .user(user -> user.text(recommendTemplate)
                        .param("mood", mood)
                        .param("titles", String.join(", ", stock.titles())))
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, "recommendations"))
                .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)   // the model must answer in the JSON schema
                .call()
                .entity(Recommendation.class);          // the JSON schema of the record, then Jackson
    }
    // end::structured-output[]
}
