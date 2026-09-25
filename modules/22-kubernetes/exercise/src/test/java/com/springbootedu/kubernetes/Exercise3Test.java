package com.springbootedu.kubernetes;

import static com.springbootedu.kubernetes.Manifests.load;
import static com.springbootedu.kubernetes.Manifests.path;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class Exercise3Test {

    @Test
    void theContainerRequestsCpu() {
        assertThat(path(Manifests.container(), "resources", "requests", "cpu")).as("requests.cpu").isNotNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void anAutoscalerScalesTheDeploymentOnCpu() {
        assertThat(Manifests.exists("hpa.yaml")).as("k8s/hpa.yaml").isTrue();
        assertThat((List<String>) load("kustomization.yaml").get("resources")).contains("hpa.yaml");

        Map<String, Object> hpa = load("hpa.yaml");
        assertThat(hpa.get("kind")).isEqualTo("HorizontalPodAutoscaler");
        assertThat(path(hpa, "spec", "scaleTargetRef", "name")).isEqualTo("bookstore");
        assertThat(path(hpa, "spec", "minReplicas")).isEqualTo(1);
        assertThat(path(hpa, "spec", "maxReplicas")).isEqualTo(4);
        assertThat(path(hpa, "spec", "metrics").toString()).contains("name=cpu").contains("averageUtilization=50");
    }
}
