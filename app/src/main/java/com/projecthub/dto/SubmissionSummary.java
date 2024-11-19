package com.projecthub.dto;

import com.projecthub.model.Submission;

public class SubmissionSummary {
    private Long id;
    private Long projectId;
    private Long studentId;
    private String filePath;
    private Integer grade;

    public SubmissionSummary() {}

    public SubmissionSummary(Submission submission) {
        this.id = submission.getId();
        this.projectId = submission.getProject() != null ? submission.getProject().getId() : null;
        this.studentId = submission.getStudent() != null ? submission.getStudent().getId() : null;
        this.filePath = submission.getFilePath();
        this.grade = submission.getGrade();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}