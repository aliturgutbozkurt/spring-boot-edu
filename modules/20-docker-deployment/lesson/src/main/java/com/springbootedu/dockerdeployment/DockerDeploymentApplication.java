package com.springbootedu.dockerdeployment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// tag::application[]
@SpringBootApplication
public class DockerDeploymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerDeploymentApplication.class, args);
    }
}
// end::application[]
