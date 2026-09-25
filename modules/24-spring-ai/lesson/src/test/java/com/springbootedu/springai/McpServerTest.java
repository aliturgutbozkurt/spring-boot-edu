package com.springbootedu.springai;

import static org.assertj.core.api.Assertions.assertThat;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

/**
 * Lesson 3.7 — any MCP client (an IDE, a desktop assistant, another application) can find and call our tool.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.ai.model.chat=none", "spring.ai.model.embedding=none",
        "spring.ai.vectorstore.pgvector.dimensions=16", "bookstore.tour.enabled=false"})
@Import(AiTestConfiguration.class)
class McpServerTest {

    @LocalServerPort
    int port;

    // tag::mcp-client[]
    @Test
    void anMcpClientListsAndCallsTheStockTool() {
        var transport = HttpClientStreamableHttpTransport.builder("http://localhost:" + port).build();   // POST /mcp
        try (McpSyncClient client = McpClient.sync(transport).build()) {
            client.initialize();

            assertThat(client.listTools().tools()).extracting(McpSchema.Tool::name).contains("stockOf");

            var result = client.callTool(McpSchema.CallToolRequest.builder("stockOf").arguments(Map.of("isbn", "9780134685991")).build());
            assertThat(result.content()).first().asString().contains("12");
        }
    }
    // end::mcp-client[]
}
