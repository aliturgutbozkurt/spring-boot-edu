package com.springbootedu.modulith;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Lessons 3.1 and 3.6 — the module structure is checked like code: by a test, without starting Spring.
 */
class ModularityTest {

    // tag::verify[]
    static final ApplicationModules MODULES = ApplicationModules.of(ModulithApplication.class);

    @Test
    void theModulesRespectTheirBoundaries() {
        MODULES.verify();                       // no cycles, no access to another module's internal packages
    }
    // end::verify[]

    @Test
    void theModulesDependOnEachOtherAsDesigned() {
        assertThat(dependenciesOf("catalog")).isEmpty();
        assertThat(dependenciesOf("order")).containsExactly("catalog");
        assertThat(dependenciesOf("inventory")).containsExactly("order");
        assertThat(dependenciesOf("notification")).containsExactly("order");
    }

    // tag::documenter[]
    @Test
    void writeDocumentation() {
        new Documenter(MODULES).writeDocumentation();     // PlantUML (C4) diagrams + module canvases

        assertThat(Path.of("target/spring-modulith-docs/components.puml")).exists();
        assertThat(Path.of("target/spring-modulith-docs/module-order.puml")).exists();
    }
    // end::documenter[]

    private static List<String> dependenciesOf(String module) {
        return MODULES.getModuleByName(module).orElseThrow()
                .getDirectDependencies(MODULES)
                .uniqueModules()
                .map(ApplicationModule::getIdentifier)
                .map(Object::toString)
                .toList();
    }
}
