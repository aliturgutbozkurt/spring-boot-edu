package com.springbootedu.springai;

import com.springbootedu.springai.stock.StockTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Lesson 3.7 — the same tool for other AI applications: the MCP server offers it at /mcp.
 */
// tag::mcp[]
@Configuration(proxyBeanMethods = false)
class McpConfiguration {

    @Bean
    ToolCallbackProvider bookstoreTools(StockTools stock) {
        return MethodToolCallbackProvider.builder().toolObjects(stock).build();
    }
}
// end::mcp[]
