package com.projecthub.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

class PersistenceDependenciesPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.dependencies {
            // JPA & Database dependencies
            implementation "org.springframework.boot:spring-boot-starter-data-jpa:${project.versions.springBoot}"
            implementation "org.springframework.data:spring-data-commons:${project.versions.springBoot}"
            
            // Database drivers
            runtimeOnly "com.h2database:h2:${project.versions.h2}"
            
            // Connection pool
            implementation "com.zaxxer:HikariCP:${project.versions.hikari}"
            
            // Validation
            implementation "org.springframework.boot:spring-boot-starter-validation:${project.versions.springBoot}"
            
            // Flyway for migrations
            implementation "org.flywaydb:flyway-core:${project.versions.flyway}"
        }
    }
}