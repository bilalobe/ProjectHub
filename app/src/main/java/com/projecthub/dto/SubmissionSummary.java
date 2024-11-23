package com.projecthub.dto;

import com.projecthub.model.Submission;

/**
 * Data Transfer Object for the Submission entity.
 * Used for transferring submission data between processes.
 */
public class SubmissionSummary {
    private final Long id;
    private final Long projectId;
    private final Long studentId;
    private final String filePath;
    private final Integer grade;

    /**
     * Default constructor.
     */
    public SubmissionSummary() {
        this.id = 0L;
        this.projectId = 0L;
        this.studentId = 0L;
        this.filePath = "";
        this.grade = 0;
    }

    /**
     * Constructs a SubmissionSummary from a Submission entity.
     *
     * @param submission the Submission entity
     */
    public SubmissionSummary(Submission submission) {
        this.id = submission.getId();
        this.projectId = submission.getProject() != null ? submission.getProject().getId() : null;
        this.studentId = submission.getStudent() != null ? submission.getStudent().getId() : null;
        this.filePath = submission.getFilePath();
        this.grade = submission.getGrade();
    }

    // Getters with JavaDoc comments
    /**
     * Gets the submission ID.
     *
     * @return the submission ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the project ID.
     *
     * @return the project ID
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Gets the student ID.
     *
     * @return the student ID
     */
    public Long getStudentId() {
        return studentId;
    }

    /**
     * Gets the file path.
     *
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Gets the grade.
     *
     * @return the grade
     */
    public Integer getGrade() {
        return grade;
    }
}