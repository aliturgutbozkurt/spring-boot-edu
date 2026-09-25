package com.springbootedu.kubernetes;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * Lesson 3.3 — rules for the manifests, checked in every build (no cluster needed): the probes use the
 * Actuator groups, resources are set, and Kubernetes waits longer than the application's graceful shutdown.
 */
class ManifestPolicyTest {

    private static final Path BASE = Path.of("../k8s/base");

    @Test
    void theProbesUseTheActuatorGroups() throws IOException {
        Map<String, Object> container = container();

        assertThat(path(container, "livenessProbe", "httpGet", "path")).isEqualTo("/actuator/health/liveness");
        assertThat(path(container, "readinessProbe", "httpGet", "path")).isEqualTo("/actuator/health/readiness");
        assertThat(container).containsKey("startupProbe");
    }

    @Test
    void cpuAndMemoryAreRequestedAndMemoryIsLimited() throws IOException {
        Map<String, Object> container = container();

        assertThat(path(container, "resources", "requests", "cpu")).isNotNull();       // the HPA needs it
        assertThat(path(container, "resources", "requests", "memory")).isNotNull();
        assertThat(path(container, "resources", "limits", "memory")).isNotNull();
    }

    // tag::grace-period[]
    @Test
    void kubernetesWaitsLongerThanTheGracefulShutdown() throws IOException {
        int gracePeriod = (Integer) path(deployment(), "spec", "template", "spec", "terminationGracePeriodSeconds");
        String shutdownTimeout = Files.readString(Path.of("src/main/resources/application.yaml"))
                .lines().filter(line -> line.contains("timeout-per-shutdown-phase")).findFirst().orElseThrow();
        int springSeconds = Integer.parseInt(shutdownTimeout.replaceAll(".*: (\\d+)s.*", "$1"));

        assertThat(gracePeriod).isGreaterThan(springSeconds);
    }
    // end::grace-period[]

    @Test
    void aRollingUpdateNeverRemovesAReadyPodFirst() throws IOException {
        assertThat(path(deployment(), "spec", "strategy", "rollingUpdate", "maxUnavailable")).isEqualTo(0);
    }

    @Test
    void everyOverlaySetsTheNamespaceForTheResourcesItAdds() throws IOException {
        for (String overlay : List.of("dev", "prod")) {
            try (InputStream in = Files.newInputStream(Path.of("../k8s/overlays", overlay, "kustomization.yaml"))) {
                Map<String, Object> kustomization = new Yaml().load(in);
                assertThat(kustomization.get("namespace")).as(overlay).isEqualTo("bookstore");
            }
        }
    }

    private static Map<String, Object> deployment() throws IOException {
        try (InputStream in = Files.newInputStream(BASE.resolve("deployment.yaml"))) {
            return new Yaml().load(in);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> container() throws IOException {
        List<Object> containers = (List<Object>) path(deployment(), "spec", "template", "spec", "containers");
        return (Map<String, Object>) containers.getFirst();
    }

    @SuppressWarnings("unchecked")
    private static Object path(Map<String, Object> node, String... keys) {
        Object current = node;
        for (String key : keys) {
            current = current == null ? null : ((Map<String, Object>) current).get(key);
        }
        return current;
    }
}
