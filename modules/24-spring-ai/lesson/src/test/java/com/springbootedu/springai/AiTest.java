package com.springbootedu.springai;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Lesson 3.8 — the setup of all AI tests: no Ollama, fake models, 16 dimensions (one per keyword).
 */
// tag::ai-test[]
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(properties = {
        "spring.ai.model.chat=none",                          // no Ollama ChatModel …
        "spring.ai.model.embedding=none",                     // … and no Ollama EmbeddingModel
        "spring.ai.vectorstore.pgvector.dimensions=16",
        "bookstore.tour.enabled=false"})
@Import(AiTestConfiguration.class)
public @interface AiTest {
}
// end::ai-test[]
