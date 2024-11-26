package com.projecthub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${app.submissions.filepath}")
    private String submissionsFilePath;

    @Value("${app.submissions.tempdir}")
    private String submissionsTempDir;

    @Bean
    public String submissionsFilePath() {
        if (submissionsFilePath == null || submissionsFilePath.isEmpty()) {
            throw new IllegalStateException("Submissions file path is not configured properly.");
        }
        return submissionsFilePath;
    }

    @Bean
    public String submissionsTempDir() {
        if (submissionsTempDir == null || submissionsTempDir.isEmpty()) {
            throw new IllegalStateException("Submissions temp directory is not configured properly.");
        }
        return submissionsTempDir;
    }
}