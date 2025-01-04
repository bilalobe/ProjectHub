package com.projecthub.base.student.domain.entity;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.shared.domain.enums.sync.SubmissionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a project submission made by a student.
 * <p>
 * Submissions are created when students complete and submit their project work.
 * Each submission can be graded and may include file attachments.
 * </p>
 * <p>
 * Business rules:
 * <ul>
 *   <li>Submissions must be associated with both a student and a project</li>
 *   <li>Submission timestamps cannot be in the future</li>
 *   <li>Grades, when provided, must be between 0 and 100</li>
 *   <li>Content is mandatory and limited to 5000 characters</li>
 *   <li>File paths, if provided, must be valid system paths</li>
 * </ul>
 * </p>
 *
 * @see Student
 * @see Project
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    indexes = {
        @Index(name = "idx_submission_timestamp", columnList = "timestamp"),
        @Index(name = "idx_submission_student", columnList = "student_id"),
        @Index(name = "idx_submission_project", columnList = "project_id")
    }
)
@Getter
@Setter
@ToString(exclude = {"student", "project"})
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
    @PastOrPresent(message = "Submission timestamp cannot be in the future")
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * The grade assigned to this submission.
     * <p>
     * Optional field representing the numerical grade (0-100) assigned to the submission.
     * Null value indicates the submission has not been graded yet.
     * </p>
     */
    @Min(value = 0, message = "Grade must be at least 0")
    @Max(value = 100, message = "Grade must be at most 100")
    private Integer grade;

    @Size(max = 1000)
    private String feedback;

    @Column(nullable = false)
    private boolean isLate = false;

    @Size(max = 500)
    private String reviewerNotes;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status = SubmissionStatus.PENDING;

    /**
     * The path to any files associated with this submission.
     * <p>
     * Optional field storing the system path to uploaded files related to this
     * submission. The path must be valid within the system's file storage structure.
     * </p>
     */
    @Size(max = 255, message = "File path must be less than 255 characters")
    private String filePath;

    /**
     * The student who made the submission.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull(message = "Student is mandatory")
    private Student student;

    /**
     * The project for which the submission was made.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull(message = "Project is mandatory")
    private Project project;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Submission that = (Submission) o;
        return Objects.equals(student, that.student) &&
            Objects.equals(project, that.project) &&
            Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, project, timestamp);
    }

}
