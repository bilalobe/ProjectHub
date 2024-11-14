package com.projecthub;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String SUBMISSIONS_FILE = "submissions.csv";

    private static List<Submission> submissions;

    public static void main(String[] args) {
        submissions = new ArrayList<>();
        try {
            int submissionId = generateId("submissionData");
            submitAssignment(submissionId, 101, 202, "path/to/file");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error generating ID: " + e.getMessage());
        }
    }

    private static void submitAssignment(int submissionId, int projectId, int studentId, String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            System.out.println("File path cannot be empty");
            return;
        }

        Submission newSubmission = new Submission(submissionId, projectId, studentId, filePath, null);
        try {
            submissions = CSVHandler.readSubmissionsFromCSV(SUBMISSIONS_FILE);
            submissions.add(newSubmission);
            CSVHandler.writeSubmissionsToCSV(submissions, SUBMISSIONS_FILE);
            System.out.println("Assignment submitted successfully! Submission ID: " + submissionId);
        } catch (IOException e) {
            System.err.println("Error handling submissions: " + e.getMessage());
        }
    }

    private static int generateId(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        byte[] digest = md.digest();
        return Integer.parseInt(bytesToHex(digest).substring(0, 7), 16);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}