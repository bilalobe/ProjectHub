package com.projecthub.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a submission made by a student for a project.
 * <p>
 * This entity is mapped to the "Submission" table in the database.
 * </p>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    indexes = {
        @Index(name = "idx_submission_timestamp", columnList = "timestamp")
    }
)
public class Submission {

    /**
     * The unique identifier for the submission.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The content of the submission.
     */
    @NotBlank(message = "Content is mandatory")
    @Size(max = 5000, message = "Content must be less than 5000 characters")
    @Column(nullable = false)
    private String content;

    /**
     * The timestamp when the submission was made.
     */
    @NotNull(message = "Timestamp is mandatory")
    @Column(nullable = false)
    private LocalDateTime timestamp;

    private Integer grade;

    @Size(max = 255, message = "File path must be less than 255 characters")
    private String filePath;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    private boolean deleted = false;

    /**
     * The student who made the submission.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @NotNull(message = "Student is mandatory")
    private Student student;

    /**
     * The project for which the submission was made.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @NotNull(message = "Project is mandatory")
    private Project project;

    // Default constructor required by JPA
    public Submission() {
    }

    // Constructor with all fields (excluding redundant IDs)
    public Submission(UUID id, Student student, Project project, String content, LocalDateTime timestamp, Integer grade) {
        this.id = id;
        this.student = student;
        this.project = project;
        this.content = content;
        this.timestamp = timestamp;
        this.grade = grade;
    }

    // Constructor without id (for new Submissions)
    public Submission(Student student, Project project, String content, LocalDateTime timestamp) {
        this.student = student;
        this.project = project;
        this.content = content;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Integer getGrade() {
        return grade;
    }

    public Student getStudent() {
        return student;
    }

    public Project getProject() {
        return project;
    }

    public String getFilePath() {
        return filePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", grade=" + grade +
                ", filePath='" + filePath + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", createdBy='" + createdBy + '\'' +
                ", deleted=" + deleted +
                ", studentId=" + (student != null ? student.getId() : null) +
                ", projectId=" + (project != null ? project.getId() : null) +
                '}';
    }
}