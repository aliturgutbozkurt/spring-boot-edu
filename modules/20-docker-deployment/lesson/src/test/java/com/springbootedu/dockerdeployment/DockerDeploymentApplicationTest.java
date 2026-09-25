package com.springbootedu.dockerdeployment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class DockerDeploymentApplicationTest {

    @Test
    void contextLoads() {
    }
}
