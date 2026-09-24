package com.springbootedu.nativeperformance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

@SpringBootApplication
public class NativePerformanceApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(NativePerformanceApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));   // exercise 2: record the startup steps
        application.run(args);
    }
}
