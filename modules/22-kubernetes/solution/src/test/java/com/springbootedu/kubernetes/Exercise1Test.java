package com.springbootedu.kubernetes;

import static com.springbootedu.kubernetes.Manifests.load;
import static com.springbootedu.kubernetes.Manifests.path;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class Exercise1Test {

    @Test
    @SuppressWarnings("unchecked")
    void theConfigMapIsGeneratedSoThatAChangeRollsThePods() {
        Map<String, Object> kustomization = load("kustomization.yaml");
        List<Map<String, Object>> generators = (List<Map<String, Object>>) kustomization.get("configMapGenerator");

        assertThat(generators).as("configMapGenerator").isNotNull()
                .anySatisfy(generator -> {
                    assertThat(generator.get("name")).isEqualTo("bookstore-config");
                    assertThat((List<String>) generator.get("literals")).anyMatch(l -> l.startsWith("BOOKSTORE_SHOP_NAME="));
                });
    }

    @Test
    @SuppressWarnings("unchecked")
    void thereIsNoHandWrittenConfigMapAnyMore() {
        List<String> resources = (List<String>) load("kustomization.yaml").get("resources");

        assertThat(resources).doesNotContain("configmap.yaml");
    }

    @Test
    void theDeploymentStillReadsTheConfigMap() {
        assertThat(path(Manifests.container(), "envFrom").toString()).contains("name=bookstore-config");
    }
}
