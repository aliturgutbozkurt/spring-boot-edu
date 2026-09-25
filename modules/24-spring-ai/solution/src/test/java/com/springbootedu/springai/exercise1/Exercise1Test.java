package com.springbootedu.springai.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.springai.AiTest;
import com.springbootedu.springai.FakeChatModel;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@AiTest
class Exercise1Test {

    @Autowired
    SuggestionAssistant assistant;

    @Autowired
    FakeChatModel model;

    @BeforeEach
    void freshModel() {
        model.reset();
    }

    @Test
    void theAnswerBecomesAListOfSuggestions() {
        model.willAnswer("""
                [{"title": "Effective Java", "reason": "Best practices."},
                 {"title": "Head First Design Patterns", "reason": "Design made easy."}]""");

        List<BookSuggestion> suggestions = assistant.suggest("clean code and design", 2);

        assertThat(suggestions).extracting(BookSuggestion::title)
                .containsExactly("Effective Java", "Head First Design Patterns");
    }

    @Test
    void thePromptComesFromTheTemplate() {
        model.willAnswer("[]");

        assistant.suggest("databases", 3);

        assertThat(model.lastPrompt().getContents())
                .contains("Suggest 3 books")
                .contains("interested in: databases")
                .contains("JSON");                                   // the format instructions of .entity(...)
    }
}
