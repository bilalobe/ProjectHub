package com.projecthub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ProjectHubApplication {

    private final Environment env;

    public ProjectHubApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        SpringApplication.run(ProjectHubApplication.class, args);
    }

    @Bean
    public String submissionsFilePath() {
        String path = env.getProperty("app.submissions.filepath", "submissions.csv");
        if (path == null || path.isEmpty()) {
            throw new IllegalStateException("Submissions file path is not configured properly.");
        }
        return path;
    }

    @Bean
    public String submissionsTempDir() {
        String tempDir = env.getProperty("app.submissions.tempdir", System.getProperty("java.io.tmpdir"));
        if (tempDir == null || tempDir.isEmpty()) {
            throw new IllegalStateException("Submissions temp directory is not configured properly.");
        }
        return tempDir;
    }
}