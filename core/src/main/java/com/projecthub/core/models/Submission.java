package com.projecthub.core.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
@Getter
@Setter
public class Submission extends BaseEntity {

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

    /**
     * Constructor with all fields (excluding redundant IDs).
     */
    public Submission(Student student, Project project, String content, LocalDateTime timestamp, Integer grade) {
        this.student = student;
        this.project = project;
        this.content = content;
        this.timestamp = timestamp;
        this.grade = grade;
    }

    /**
     * Constructor without id (for new Submissions).
     */
    public Submission(Student student, Project project, String content, LocalDateTime timestamp) {
        this.student = student;
        this.project = project;
        this.content = content;
        this.timestamp = timestamp;
    }
}