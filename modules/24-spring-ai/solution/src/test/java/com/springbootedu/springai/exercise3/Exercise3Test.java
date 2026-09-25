package com.springbootedu.springai.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.springai.AiTest;
import com.springbootedu.springai.FakeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;

@AiTest
class Exercise3Test {

    @Autowired
    OrderTools tools;

    @Autowired
    SupportAssistant assistant;

    @Autowired
    FakeChatModel model;

    @Test
    void theToolAnswersWithTheStatus() {
        assertThat(tools.orderStatus("A-1001")).startsWith("SHIPPED");
        assertThat(tools.orderStatus("X-9")).isEqualTo("UNKNOWN ORDER");
    }

    @Test
    void theToolIsDescribedForTheModel() {
        ToolCallback[] callbacks = ToolCallbacks.from(tools);

        assertThat(callbacks).hasSize(1);
        assertThat(callbacks[0].getToolDefinition().name()).isEqualTo("orderStatus");
        assertThat(callbacks[0].getToolDefinition().description()).containsIgnoringCase("status");
        assertThat(callbacks[0].getToolDefinition().inputSchema()).contains("orderId").contains("A-1001");
    }

    @Test
    void theAssistantOffersTheToolToTheModel() {
        model.reset();

        assistant.help("Where is my order A-1001?");

        var options = (ToolCallingChatOptions) model.lastPrompt().getOptions();
        assertThat(options.getToolCallbacks()).extracting(callback -> callback.getToolDefinition().name())
                .contains("orderStatus");
    }
}
