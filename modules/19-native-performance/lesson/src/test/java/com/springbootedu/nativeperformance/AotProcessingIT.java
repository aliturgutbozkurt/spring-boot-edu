package com.springbootedu.nativeperformance;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Lessons 3.2–3.4 — runs after "package": checks what process-aot generated at build time.
 */
class AotProcessingIT {

    private static final Path AOT = Path.of("target/spring-aot/main");
    private static final Path SOURCES = AOT.resolve("sources/com/springbootedu/nativeperformance");
    private static final Path METADATA = AOT.resolve(
            "resources/META-INF/native-image/com.springbootedu/19-native-performance-lesson/reachability-metadata.json");

    @Test
    void beanDefinitionsAreGeneratedCode() {
        assertThat(SOURCES.resolve("NativePerformanceApplication__BeanFactoryRegistrations.java")).exists();
        assertThat(SOURCES.resolve("book/BookController__BeanDefinitions.java")).exists();
    }

    @Test
    void theRepositoryImplementationIsGeneratedCode() throws IOException {
        Path repository = SOURCES.resolve("book/BookRepositoryImpl__AotRepository.java");

        assertThat(repository).exists();
        assertThat(Files.readString(repository))
                .contains("findByTitleContainingIgnoreCaseOrderByTitle")
                .contains("publishedSince");
    }

    @Test
    void ourRuntimeHintsAreInTheNativeImageMetadata() {
        JsonNode metadata = JsonMapper.builder().build().readTree(METADATA.toFile());

        assertThat(textValues(metadata.get("reflection"), "type"))
                .contains("com.springbootedu.nativeperformance.price.TurkishLiraFormat",
                        "com.springbootedu.nativeperformance.price.EuroFormat");
        assertThat(textValues(metadata.get("resources"), "glob")).contains("quotes/*.txt");
    }

    /** The string values of one field of all entries (a "type" can also be an object, e.g. for proxies). */
    private static List<String> textValues(JsonNode entries, String field) {
        return entries.valueStream()
                .map(entry -> entry.get(field))
                .filter(value -> value != null && value.isString())
                .map(JsonNode::asString)
                .toList();
    }
}
