package com.springbootedu.springai.stock;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.5 — a tool: the model may call this method when it needs the stock of a book.
 * The descriptions are part of the prompt: they tell the model when and how to use the tool.
 */
// tag::tool[]
@Component
public class StockTools {

    private final JdbcClient jdbc;

    StockTools(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Tool(description = "Returns how many copies of a book are in stock. Use it for every question about availability.")
    public int stockOf(@ToolParam(description = "the 13-digit ISBN of the book") String isbn) {
        return jdbc.sql("SELECT available FROM stock WHERE isbn = ?").param(isbn).query(Integer.class)
                .optional().orElse(0);
    }
    // end::tool[]

    public java.util.List<String> titles() {
        return jdbc.sql("SELECT title FROM stock ORDER BY title").query(String.class).list();
    }
}
