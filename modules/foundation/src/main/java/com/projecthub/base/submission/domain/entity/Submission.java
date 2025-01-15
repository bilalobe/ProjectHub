package com.projecthub.base.submission.domain.entity;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.shared.domain.entity.PreActivatable;
import com.projecthub.base.shared.domain.enums.status.ActivationStatus;
import com.projecthub.base.shared.domain.event.DomainEvent;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.shared.domain.enums.sync.SubmissionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
@Table(name = "submissions", indexes = {
    @Index(name = "idx_submission_timestamp", columnList = "timestamp"),
    @Index(name = "idx_submission_student", columnList = "student_id"),
    @Index(name = "idx_submission_project", columnList = "project_id"),
    @Index(name = "idx_submission_number", columnList = "submissionNumber", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
@ToString(exclude = {"student", "project"})
@EqualsAndHashCode(of = "submissionNumber", callSuper = false)
public class Submission extends BaseEntity implements PreActivatable {

    // Business identifier
    @Column(nullable = false, unique = true, updatable = false)
    private String submissionNumber;

    /**
     * The content of the submission.
     */
    @NotBlank
    @Size(max = 5000)
    @Column(nullable = false)
    private String content;

    /**
     * The timestamp when the submission was made.
     */
    @NotNull
    @PastOrPresent
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * The grade assigned to this submission.
     * <p>
     * Optional field representing the numerical grade (0-100) assigned to the submission.
     * Null value indicates the submission has not been graded yet.
     * </p>
     */
    @Min(0) @Max(100)
    private Integer grade;

    @Size(max = 1000)
    private String feedback;

    @Column(nullable = false)
    private boolean isLate;

    @Size(max = 500)
    private String reviewerNotes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus submissionStatus = SubmissionStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivationStatus status = ActivationStatus.PENDING;

    /**
     * The path to any files associated with this submission.
     * <p>
     * Optional field storing the system path to uploaded files related to this
     * submission. The path must be valid within the system's file storage structure.
     * </p>
     */
    @Size(max = 255)
    @Pattern(regexp = "^[a-zA-Z]:\\\\(?:[^\\\\/:*?\"<>|\\r\\n]+\\\\)*[^\\\\/:*?\"<>|\\r\\n]*$")
    private String filePath;

    /**
     * The student who made the submission.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Student student;

    /**
     * The project for which the submission was made.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NotNull
    private Project project;

    @Singular("event")
    @Builder.Default
    @Transient
    private final List<DomainEvent> events = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    // Domain methods for status transitions
    @Override
    public void activate() {
        if (status != ActivationStatus.PENDING) {
            throw new IllegalStateException("Can only activate pending submissions");
        }
        this.status = ActivationStatus.ACTIVE;
        registerEvent(new SubmissionDomainEvent.Submitted(
            UUID.randomUUID(),
            this.getId(),
            this.submissionNumber,
            Instant.now()
        ));
    }

    @Override
    public void deactivate() {
        if (status != ActivationStatus.ACTIVE) {
            throw new IllegalStateException("Can only deactivate active submissions");
        }
        this.status = ActivationStatus.INACTIVE;
    }

    @Override
    public boolean isActive() {
        return status == ActivationStatus.ACTIVE;
    }

    public void submit() {
        if (status != ActivationStatus.ACTIVE) {
            throw new IllegalStateException("Can only submit active submissions");
        }
        this.submissionStatus = SubmissionStatus.SUBMITTED;
        this.timestamp = LocalDateTime.now();
        
        registerEvent(new SubmissionDomainEvent.Submitted(
            UUID.randomUUID(),
            this.getId(),
            this.submissionNumber,
            Instant.now()
        ));
    }

    public void grade(int grade, String feedback) {
        Objects.requireNonNull(feedback, "Feedback cannot be null");
        if (submissionStatus != SubmissionStatus.SUBMITTED) {
            throw new IllegalStateException("Can only grade submitted submissions");
        }
        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("Grade must be between 0 and 100");
        }
        
        this.grade = grade;
        this.feedback = feedback.trim();
        this.submissionStatus = SubmissionStatus.GRADED;
        
        registerEvent(new SubmissionDomainEvent.Graded(getId(), submissionNumber, grade, feedback));
    }

    @Override
    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(events);
    }

    @Override
    public void clearDomainEvents() {
        events.clear();
    }

    @Override
    protected void registerEvent(DomainEvent event) {
        events.add(Objects.requireNonNull(event));
    }

    // Factory method
    public static Submission create(
            String content,
            Student student,
            Project project,
            String filePath,
            boolean isLate) {
        Objects.requireNonNull(content, "Content cannot be null");
        Objects.requireNonNull(student, "Student cannot be null");
        Objects.requireNonNull(project, "Project cannot be null");

        Submission submission = Submission.builder()
            .submissionNumber(generateSubmissionNumber())
            .content(content.trim())
            .student(student)
            .project(project)
            .filePath(filePath != null ? filePath.trim() : null)
            .isLate(isLate)
            .timestamp(LocalDateTime.now())
            .submissionStatus(SubmissionStatus.PENDING)
            .status(ActivationStatus.PENDING)
            .build();

        submission.registerEvent(new SubmissionDomainEvent.Created(
            UUID.randomUUID(),
            submission.getId(),
            submission.getSubmissionNumber(),
            submission.getStudent().getId(),
            submission.getProject().getId(),
            Instant.now()
        ));

        return submission;
    }

    private static String generateSubmissionNumber() {
        return String.format("SUB-%d-%06d",
            LocalDateTime.now().getYear(),
            Math.abs(UUID.randomUUID().hashCode() % 1000000)
        );
    }
}