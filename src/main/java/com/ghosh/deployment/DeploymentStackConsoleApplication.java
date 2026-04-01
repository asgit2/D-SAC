package com.ghosh.deployment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeploymentStackConsoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeploymentStackConsoleApplication.class, args);
    }
}
