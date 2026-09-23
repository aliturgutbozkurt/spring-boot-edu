package com.springbootedu.configuration.profiles;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.configuration.store.StoreProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

/**
 * Lesson 3.4 — the "local" profile group activates "dev" and "demo".
 */
@SpringBootTest
@ActiveProfiles("local")
class ProfileGroupTest {

    @Autowired
    StoreProperties store;

    @Autowired
    Environment environment;

    @Test
    void theGroupExpandsToItsMembers() {
        assertThat(environment.getActiveProfiles()).containsExactly("local", "dev", "demo");
    }

    @Test
    void profileSpecificFileOverridesTheDefault() {
        assertThat(store.name()).isEqualTo("Kitapçı (DEV)");            // application-dev.yaml
    }

    @Test
    void profileDocumentInsideApplicationYamlIsApplied() {
        assertThat(store.banner()).isEqualTo("DEMO — veriler gerçek değil / demo data");   // on-profile: demo
    }
}
