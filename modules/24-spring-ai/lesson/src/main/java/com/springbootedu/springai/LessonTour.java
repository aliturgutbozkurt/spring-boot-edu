package com.springbootedu.springai;

import com.springbootedu.springai.assistant.BookAssistant;
import com.springbootedu.springai.rag.BookCatalogIngestion;
import com.springbootedu.springai.rag.BookQuestions;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.stereotype.Component;

/**
 * Runs the lesson's examples once (section 3) against the local Ollama.
 * Start it with: ./mvnw -pl modules/24-spring-ai/lesson spring-boot:run   (Docker must be running; the first
 * start downloads the models)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour implements ApplicationRunner {

    private final BookAssistant assistant;
    private final BookCatalogIngestion ingestion;
    private final BookQuestions questions;

    LessonTour(BookAssistant assistant, BookCatalogIngestion ingestion, BookQuestions questions) {
        this.assistant = assistant;
        this.ingestion = ingestion;
        this.questions = questions;
    }

    @Override
    public void run(ApplicationArguments args) {
        section("3.2 A question");
        print(assistant.ask("tour", "In one sentence: what is Spring Boot?"));

        section("3.3 Structured output");
        print(assistant.recommend("curious about how big systems stay consistent"));

        section("3.4 Memory");
        assistant.ask("tour", "My favourite author is Joshua Bloch.");
        print(assistant.ask("tour", "Who is my favourite author?"));

        section("3.5 A tool call");
        print(assistant.ask("tour", "How many copies of the book with ISBN 9781449373320 are in stock?"));

        section("3.6 RAG over the book descriptions");
        print("stored chunks: " + ingestion.ingest());
        print(questions.answer("Which of our books is best for a beginner who wants to learn software design?"));
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    private static void print(Object line) {
        System.out.println("  " + line);
    }
}
