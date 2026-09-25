package com.springbootedu.kubernetes;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Given — reads the exercise manifests in k8s/ as plain maps.
 */
public final class Manifests {

    public static final Path K8S = Path.of("k8s");

    private Manifests() {
    }

    public static Map<String, Object> load(String file) {
        try (InputStream in = Files.newInputStream(K8S.resolve(file))) {
            return new Yaml().load(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static boolean exists(String file) {
        return Files.exists(K8S.resolve(file));
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> container() {
        List<Object> containers = (List<Object>) path(load("deployment.yaml"), "spec", "template", "spec", "containers");
        return (Map<String, Object>) containers.getFirst();
    }

    /** Follows keys through nested maps; null when a key is missing. */
    @SuppressWarnings("unchecked")
    public static Object path(Map<String, Object> node, String... keys) {
        Object current = node;
        for (String key : keys) {
            current = current instanceof Map<?, ?> map ? ((Map<String, Object>) map).get(key) : null;
        }
        return current;
    }
}
