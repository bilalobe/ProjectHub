package com.projecthub.dto;

import com.opencsv.bean.CsvBindByName;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for Submission summary information.
 * Used for transferring submission data between processes.
 */
public class SubmissionDTO {

    /**
     * The unique identifier of the submission.
     */
    @CsvBindByName
    @NotNull(message = "Submission ID cannot be null")
    private UUID id;

    @CsvBindByName
    @NotNull(message = "Project ID cannot be null")
    private UUID projectId;

    @CsvBindByName
    @NotNull(message = "Student ID cannot be null")
    private UUID studentId;

    @CsvBindByName
    @NotBlank(message = "Content is mandatory")
    private String content;

    @CsvBindByName
    @NotNull(message = "Timestamp cannot be null")
    private LocalDateTime timestamp;

    @CsvBindByName
    private Integer grade;

    @CsvBindByName
    private String projectName;

    @CsvBindByName
    private String studentFirstName;

    @CsvBindByName
    private String studentLastName;

    // No-argument constructor
    public SubmissionDTO() {}


    /**
     * Gets the unique identifier of the submission.
     *
     * @return the submission's unique identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the submission.
     *
     * @param id the submission's unique identifier
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Gets the project ID.
     *
     * @return the project ID
     */
    public UUID getProjectId() {
        return projectId;
    }

    /**
     * Sets the project ID.
     *
     * @param projectId the project ID
     */
    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    /**
     * Gets the student ID.
     *
     * @return the student ID
     */
    public UUID getStudentId() {
        return studentId;
    }

    /**
     * Sets the student ID.
     *
     * @param studentId the student ID
     */
    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    /**
     * Gets the content of the submission.
     *
     * @return the content of the submission
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the submission.
     *
     * @param content the content of the submission
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the timestamp of the submission.
     *
     * @return the timestamp of the submission
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the submission.
     *
     * @param timestamp the timestamp of the submission
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the grade of the submission.
     *
     * @return the grade of the submission
     */
    public Integer getGrade() {
        return grade;
    }

    /**
     * Sets the grade of the submission.
     *
     * @param grade the grade of the submission
     */
    public void setGrade(Integer grade) {
        this.grade = grade;
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
     * Sets the project name.
     *
     * @param projectName the project name
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Gets the student's first name.
     *
     * @return the student's first name
     */
    public String getStudentFirstName() {
        return studentFirstName;
    }

    /**
     * Sets the student's first name.
     *
     * @param studentFirstName the student's first name
     */
    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    /**
     * Gets the student's last name.
     *
     * @return the student's last name
     */
    public String getStudentLastName() {
        return studentLastName;
    }

    /**
     * Sets the student's last name.
     *
     * @param studentLastName the student's last name
     */
    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }
}