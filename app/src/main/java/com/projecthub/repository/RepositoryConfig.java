package com.projecthub.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.projecthub.repository.jpa")
public class RepositoryConfig {
    // Configuration for repositories if needed
}