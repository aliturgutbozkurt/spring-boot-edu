package com.springbootedu.springai.assistant;

import com.springbootedu.springai.rag.BookQuestions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3 — HTTP endpoints to try the assistant (see requests.http).
 */
@RestController
class AssistantController {

    private final BookAssistant assistant;
    private final BookQuestions questions;

    AssistantController(BookAssistant assistant, BookQuestions questions) {
        this.assistant = assistant;
        this.questions = questions;
    }

    @GetMapping("/api/assistant/ask")
    String ask(@RequestParam(defaultValue = "web") String conversation, @RequestParam String question) {
        return assistant.ask(conversation, question);
    }

    @GetMapping("/api/assistant/recommend")
    Recommendation recommend(@RequestParam String mood) {
        return assistant.recommend(mood);
    }

    @GetMapping("/api/assistant/books")
    String aboutBooks(@RequestParam String question) {
        return questions.answer(question);
    }
}
