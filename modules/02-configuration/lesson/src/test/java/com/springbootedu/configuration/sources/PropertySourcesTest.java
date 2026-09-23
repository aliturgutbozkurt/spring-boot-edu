package com.springbootedu.configuration.sources;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.configuration.store.StoreProperties;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;

/**
 * Lesson 3.1 — where does a value come from, and who wins?
 */
@SpringBootTest(args = "--bookstore.store.name=Komut Satırı Kitapçısı")
class PropertySourcesTest {

    @Autowired
    StoreProperties store;

    @Autowired
    PropertyOrigins origins;

    @Test
    void commandLineArgumentsOverrideApplicationYaml() {
        assertThat(store.name()).isEqualTo("Komut Satırı Kitapçısı");
        assertThat(origins.sourceOf("bookstore.store.name")).isEqualTo("commandLineArgs");
    }

    @Test
    void valuesWithoutOverrideComeFromApplicationYaml() {
        assertThat(store.supportEmail()).isEqualTo("destek@kitapci.example");
        assertThat(origins.sourceOf("bookstore.store.support-email")).contains("application.yaml");
    }

    @Test
    void environmentVariablesBindWithRelaxedNames() {
        var environment = new StandardEnvironment();
        // Boot applies the env-var name mapping to the source named "systemEnvironment"
        environment.getPropertySources().replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                        Map.of("BOOKSTORE_STORE_SUPPORTEMAIL", "env@kitapci.example")));

        String bound = Binder.get(environment).bind("bookstore.store.support-email", String.class).get();

        assertThat(bound).isEqualTo("env@kitapci.example");
    }
}
