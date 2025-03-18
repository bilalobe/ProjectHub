package com.projecthub.auth.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration class that defines the authentication module boundaries.
 * This module provides all authentication and authorization functionality,
 * including Fortress RBAC integration.
 */
@Configuration
@ComponentScan(basePackages = "com.projecthub.auth")
@EntityScan(basePackages = "com.projecthub.auth.domain.entity")
@EnableJpaRepositories(basePackages = "com.projecthub.auth.infrastructure.repository")
public class AuthModule {
    // Module configuration goes here if needed
}