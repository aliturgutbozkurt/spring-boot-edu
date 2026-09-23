package com.springbootedu.rediscaching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// tag::application[]
@SpringBootApplication
public class RedisCachingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisCachingApplication.class, args);
    }
}
// end::application[]
