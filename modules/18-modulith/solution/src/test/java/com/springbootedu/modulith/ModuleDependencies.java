package com.springbootedu.modulith;

import java.util.List;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Test helper: the names of the modules a module depends on directly.
 */
public final class ModuleDependencies {

    private static final ApplicationModules MODULES = ApplicationModules.of(ModulithApplication.class);

    private ModuleDependencies() {
    }

    public static List<String> of(String module) {
        return MODULES.getModuleByName(module).orElseThrow()
                .getDirectDependencies(MODULES)
                .uniqueModules()
                .map(ApplicationModule::getIdentifier)
                .map(Object::toString)
                .toList();
    }
}
