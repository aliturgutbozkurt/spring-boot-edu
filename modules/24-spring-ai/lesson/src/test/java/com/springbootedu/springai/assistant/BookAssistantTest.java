package com.springbootedu.springai.assistant;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.springai.AiTest;
import com.springbootedu.springai.FakeChatModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Lessons 3.2–3.5 — what the application sends to the model: system prompt, memory, tools, format instructions.
 */
@AiTest
class BookAssistantTest {

    @Autowired
    BookAssistant assistant;

    @Autowired
    FakeChatModel model;

    @BeforeEach
    void freshModel() {
        model.reset();
    }

    @Test
    void theSystemPromptGoesWithEveryQuestion() {
        model.willAnswer("Effective Java is a good start.");

        assertThat(assistant.ask("c-1", "Which book for Java?")).isEqualTo("Effective Java is a good start.");
        assertThat(model.lastPrompt().getInstructions().getFirst().getMessageType()).isEqualTo(MessageType.SYSTEM);
        assertThat(model.lastPrompt().getInstructions().getFirst().getText()).contains("online bookstore");
    }

    // tag::memory-test[]
    @Test
    void theMemoryRemembersTheConversation() {
        assistant.ask("c-2", "My name is Ayşe.");
        assistant.ask("c-2", "What is my name?");

        assertThat(model.lastPrompt().getContents()).contains("My name is Ayşe.");    // the first question went along
        assistant.ask("c-3", "What is my name?");
        assertThat(model.lastPrompt().getContents()).doesNotContain("Ayşe");         // another conversation
    }
    // end::memory-test[]

    @Test
    void theModelIsOfferedTheStockTool() {
        assistant.ask("c-4", "Is Spring in Action available?");

        var options = (ToolCallingChatOptions) model.lastPrompt().getOptions();
        assertThat(options.getToolCallbacks()).extracting(callback -> callback.getToolDefinition().name())
                .contains("stockOf");
    }

    @Test
    void theAnswerBecomesARecord() {
        model.willAnswer("""
                {"title": "Head First Design Patterns", "author": "Eric Freeman", "reason": "Light and visual."}""");

        Recommendation recommendation = assistant.recommend("tired but curious");

        assertThat(recommendation.title()).isEqualTo("Head First Design Patterns");
        assertThat(model.lastPrompt().getContents())
                .contains("tired but curious")                     // the template parameter
                .contains("Effective Java")                        // the titles from the database
                .contains("JSON");                                 // the format instructions of .entity(...)
    }
}
