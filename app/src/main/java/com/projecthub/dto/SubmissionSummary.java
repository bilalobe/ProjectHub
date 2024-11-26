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
    private final String content;
    private final String timestamp;
    private final Integer grade;
    private final String projectName;
    private final String studentName;

    /**
     * Default constructor.
     */
    public SubmissionSummary() {
        this.id = 0L;
        this.projectId = 0L;
        this.studentId = 0L;
        this.content = "";
        this.timestamp = "";
        this.grade = 0;
        this.projectName = "";
        this.studentName = "";
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
        this.content = submission.getContent();
        this.timestamp = submission.getTimestamp();
        this.grade = submission.getGrade();
        this.projectName = submission.getProject() != null ? submission.getProject().getName() : "";
        this.studentName = submission.getStudent() != null ? submission.getStudent().getName() : "";
    }

    /**
     * Constructs a SubmissionSummary with the specified details.
     *
     * @param id the submission ID
     * @param content the submission content
     * @param timestamp the submission timestamp
     * @param grade the submission grade
     * @param projectId the project ID associated with the submission
     * @param studentId the student ID associated with the submission
     * @param projectName the project name associated with the submission
     * @param studentName the student name associated with the submission
     */
    public SubmissionSummary(Long id, String content, String timestamp, Integer grade, Long projectId, Long studentId, String projectName, String studentName) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.grade = grade;
        this.projectId = projectId;
        this.studentId = studentId;
        this.projectName = projectName;
        this.studentName = studentName;
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
     * Gets the project name.
     *
     * @return the project name
     */
    public String getProjectName() {
        return projectName;
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
     * Gets the student name.
     *
     * @return the student name
     */
    public String getStudentName() {
        return studentName;
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
     * Gets the content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
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