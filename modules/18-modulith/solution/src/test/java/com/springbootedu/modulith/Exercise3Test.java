package com.springbootedu.modulith;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Exercise3Test {

    @Test
    void theOrderModuleNoLongerKnowsTheInventory() {
        assertThat(ModuleDependencies.of("order")).doesNotContain("inventory");
        assertThat(ModuleDependencies.of("inventory")).contains("order");      // through the event type
    }
}
