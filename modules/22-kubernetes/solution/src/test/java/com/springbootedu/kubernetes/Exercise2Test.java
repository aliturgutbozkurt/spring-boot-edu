package com.springbootedu.kubernetes;

import static com.springbootedu.kubernetes.Manifests.load;
import static com.springbootedu.kubernetes.Manifests.path;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class Exercise2Test {

    @Test
    void aRollingUpdateKeepsAllReadyPodsUntilTheNewOnesAreReady() {
        Map<String, Object> deployment = load("deployment.yaml");

        assertThat(path(deployment, "spec", "strategy", "type")).isEqualTo("RollingUpdate");
        assertThat(path(deployment, "spec", "strategy", "rollingUpdate", "maxUnavailable")).isEqualTo(0);
        assertThat(path(deployment, "spec", "strategy", "rollingUpdate", "maxSurge")).isEqualTo(1);
    }

    @Test
    void aBrokenVersionNeverBecomesReady() {
        assertThat(path(Manifests.container(), "readinessProbe", "httpGet", "path"))
                .isEqualTo("/actuator/health/readiness");
    }

    @Test
    void thereAreOldVersionsToRollBackTo() {
        assertThat((Integer) path(load("deployment.yaml"), "spec", "revisionHistoryLimit")).isGreaterThanOrEqualTo(3);
    }
}
