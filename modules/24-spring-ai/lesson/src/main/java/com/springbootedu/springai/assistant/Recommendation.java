package com.springbootedu.springai.assistant;

/**
 * Lesson 3.3 — structured output: the model's answer is converted into this record.
 */
public record Recommendation(String title, String author, String reason) {
}
