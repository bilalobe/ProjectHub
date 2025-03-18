package com.projecthub;

import org.springframework.boot.SpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The main class for the Project Hub application.
 */
@EnableJpaRepositories(basePackages = "com.projecthub.repository")
public class ProjectHubApplication {

    public ProjectHubApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(ProjectHubApplication.class, args);
    }
}
