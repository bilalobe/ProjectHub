package com.projecthub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    /**
     * Generates a unique ID based on input data.
     * @param data The input string to generate ID from
     * @return A numeric ID derived from the hash of the input
     */
    public static int generateUniqueId(String data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Input data cannot be null or empty.");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            byte[] digest = md.digest();
            String hexString = bytesToHex(digest);
            String idString = hexString.substring(0, Math.min(7, hexString.length()));
            return Integer.parseInt(idString, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available.", e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to parse hash to integer.", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        if (hash == null || hash.length == 0) {
            throw new IllegalArgumentException("Hash byte array cannot be null or empty.");
        }
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}