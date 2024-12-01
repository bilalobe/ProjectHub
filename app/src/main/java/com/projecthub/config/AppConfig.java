package com.projecthub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private final CsvProperties csvProperties;


    public AppConfig(CsvProperties csvProperties) {
        this.csvProperties = csvProperties;
    }

    @Bean
    public String submissionsFilePath() {
        String path = csvProperties.getSubmissionsFilepath();
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("Submissions file path is not configured properly.");
        }
        return path;
    }

    @Bean
    public String submissionsTempDir() {
        String path = csvProperties.getSubmissionsFilepath();
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("Submissions temp directory is not configured properly.");
        }
        return path;
    }

    @Bean
    public String teamsFilePath() {
        String path = csvProperties.getTeamsFilepath();
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("Teams file path is not configured properly.");
        }
        return path;
    }

    @Bean
    public String cohortsFilePath() {
        String path = csvProperties.getCohortsFilepath();
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("Cohorts file path is not configured properly.");
        }
        return path;
    }

    @Bean
    public String studentsFilePath() {
        String path = csvProperties.getStudentsFilepath();
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("Students file path is not configured properly.");
        }
        return path;
    }

    @Bean
    public String projectsFilePath() {
        String path = csvProperties.getProjectsFilepath();
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("Projects file path is not configured properly.");
        }
        return path;
    }

    @Bean
    public String componentsFilePath() {
        String path = csvProperties.getComponentsFilepath();
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("Components file path is not configured properly.");
        }
        return path;
    }
}