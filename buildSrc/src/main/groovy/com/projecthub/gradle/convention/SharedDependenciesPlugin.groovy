package com.projecthub.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

class SharedDependenciesPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.dependencies {
            // Spring Boot Platform
            implementation platform("org.springframework.boot:spring-boot-dependencies:${project.ext.versions.springBoot}")
            
            // Modulith
            implementation "org.springframework.modulith:spring-modulith-starter-core:${project.ext.versions.springModulith}"
            implementation "org.springframework.modulith:spring-modulith-api:${project.ext.versions.springModulith}"
            implementation "org.springframework.modulith:spring-modulith-events-core:${project.ext.versions.springModulith}"
            
            // Testing
            testImplementation "org.springframework.boot:spring-boot-starter-test:${project.ext.versions.springBoot}"
            testImplementation "org.springframework.modulith:spring-modulith-test:${project.ext.versions.springModulith}"
            testImplementation "org.springframework.modulith:spring-modulith-docs:${project.ext.versions.springModulith}"
            
            // Common Tools
            implementation "org.mapstruct:mapstruct:${project.ext.versions.mapstruct}"
            compileOnly "org.projectlombok:lombok:${project.ext.versions.lombok}"
            annotationProcessor "org.projectlombok:lombok:${project.ext.versions.lombok}"
            annotationProcessor "org.mapstruct:mapstruct-processor:${project.ext.versions.mapstruct}"
        }
    }
}