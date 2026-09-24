package com.springbootedu.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class Exercise1Test {

    @Test
    void theModulesRespectTheirBoundaries() {
        // detectViolations() checks every time; verify() is skipped when the (cached) model was verified before,
        // for example by an @ApplicationModuleTest earlier in the same JVM
        ApplicationModules.of(ModulithApplication.class).detectViolations().throwIfPresent();
    }
}
