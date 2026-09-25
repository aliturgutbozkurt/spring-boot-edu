package com.springbootedu.kubernetes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// tag::application[]
@SpringBootApplication
public class KubernetesApplication {

    public static void main(String[] args) {
        SpringApplication.run(KubernetesApplication.class, args);
    }
}
// end::application[]
