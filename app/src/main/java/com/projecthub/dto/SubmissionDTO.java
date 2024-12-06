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
     * Custom constructor for creating a SubmissionDTO with specific values.
     *
     * @param id              the submission ID
     * @param projectId       the project ID
     * @param studentId       the student ID
     * @param content         the content of the submission
     * @param timestamp       the timestamp of the submission
     * @param grade           the grade of the submission
     * @param projectName     the project name
     * @param studentFirstName the student's first name
     * @param studentLastName  the student's last name
     */
    public SubmissionDTO(UUID id, UUID projectId, UUID studentId, String content, LocalDateTime timestamp, Integer grade, String projectName, String studentFirstName, String studentLastName) {
        this.id = id;
        this.projectId = projectId;
        this.studentId = studentId;
        this.content = content;
        this.timestamp = timestamp;
        this.grade = grade;
        this.projectName = projectName;
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
    }

    // Getters and setters...

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }
}