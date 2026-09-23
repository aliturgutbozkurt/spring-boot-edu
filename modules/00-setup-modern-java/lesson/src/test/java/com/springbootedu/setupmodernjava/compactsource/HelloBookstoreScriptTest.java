package com.springbootedu.setupmodernjava.compactsource;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * Lesson 3.9 — a compact source file runs directly with the java launcher, no class, no build.
 */
class HelloBookstoreScriptTest {

    @Test
    void runsWithTheJavaLauncher() throws Exception {
        var java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        var process = new ProcessBuilder(java, "-Dstdout.encoding=UTF-8", "src/scripts/HelloBookstore.java", "Ayşe")
                .redirectErrorStream(true)
                .start();

        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        assertThat(process.waitFor()).isZero();
        assertThat(output).contains("Merhaba Ayşe! / Hello Ayşe!").contains("3 kitap / books");
    }
}
