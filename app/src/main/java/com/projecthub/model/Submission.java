package com.projecthub.model;

public class Submission {
    private final int submissionId;
    private final int projectId;
    private final int studentId;
    private final String filePath;
    private final Integer grade;

    public Submission(int submissionId, int projectId, int studentId, String filePath, Integer grade) {
        this.submissionId = submissionId;
        this.projectId = projectId;
        this.studentId = studentId;
        this.filePath = filePath;
        this.grade = grade;
    }

    // Getters and setters
    public int getSubmissionId() {
        return submissionId;
    }

    public int getProjectId() {
        return projectId;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getFilePath() {
        return filePath;
    }

    public Integer getGrade() {
        return grade;
    }
}