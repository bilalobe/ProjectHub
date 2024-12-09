package com.projecthub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.projecthub.config.CsvProperties;

@SpringBootApplication
@EnableConfigurationProperties(CsvProperties.class)
@EnableJpaRepositories(basePackages = "com.projecthub.repository.jpa")
public class ProjectHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectHubApplication.class, args);
    }
}