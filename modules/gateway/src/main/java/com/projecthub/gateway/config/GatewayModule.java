package com.projecthub.gateway.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.projecthub.auth.config.AuthModule;

/**
 * Configuration class that defines the gateway module boundaries.
 * This module provides API routing, rate limiting, and security enforcement
 * by integrating with the auth module for RBAC decisions.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.projecthub.gateway")
@Import(AuthModule.class)
public class GatewayModule {
    // Module configuration goes here if needed
}