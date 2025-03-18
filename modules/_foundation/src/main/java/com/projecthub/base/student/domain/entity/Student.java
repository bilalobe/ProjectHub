package com.projecthub.base.student.domain.entity;

    import com.projecthub.base.shared.domain.PreActivatable;
    import com.projecthub.base.shared.domain.entity.BaseEntity;
    import com.projecthub.base.shared.domain.enums.status.ActivationStatus;
    import com.projecthub.base.student.domain.event.StudentDomainEvent;
    import com.projecthub.base.team.domain.entity.Team;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Pattern;
    import jakarta.validation.constraints.Size;
    import lombok.Builder;
    import lombok.EqualsAndHashCode;
    import lombok.Getter;
    import lombok.ToString;
    import lombok.experimental.SuperBuilder;
    import org.jetbrains.annotations.NonNls;
    import org.springframework.data.jpa.domain.support.AuditingEntityListener;

    import java.time.Instant;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.*;

    @Entity
    @EntityListeners(AuditingEntityListener.class)
    @Table(name = "students", indexes = {
        @Index(name = "idx_student_email", columnList = "email"),
        @Index(name = "idx_student_last_name", columnList = "lastName"),
        @Index(name = "idx_student_team", columnList = "team_id"),
        @Index(name = "idx_student_student_id", columnList = "studentId")
    }, uniqueConstraints = {
        @UniqueConstraint(name = "uc_student_email", columnNames = "email"),
        @UniqueConstraint(name = "uc_student_student_id", columnNames = "studentId")
    })
    @Getter
    @ToString(exclude = "team")
    @EqualsAndHashCode(of = "studentId", callSuper = true)
    @SuperBuilder(toBuilder = true)
    public class Student extends BaseEntity implements PreActivatable {

        @Builder.Default
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ActivationStatus status = ActivationStatus.PENDING;

        @Builder.Default
        @Column(nullable = false, updatable = false)
        private UUID studentId = UUID.randomUUID();

        @NotBlank(message = "First name is mandatory")
        @Size(max = 50)
        @Column(nullable = false)
        private String firstName;

        @NotBlank(message = "Last name is mandatory")
        @Size(max = 50)
        @Column(nullable = false)
        private String lastName;

        @Size(max = 50)
        private String middleName;

        @Email
        @NotBlank(message = "Email is mandatory")
        @Column(nullable = false)
        private String email;

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
        private String phoneNumber;

        @Size(max = 255)
        private String emergencyContact;

        @Column(nullable = false)
        private LocalDate enrollmentDate;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "team_id")
        private Team team;

        @Builder.Default
        @Transient
        private final List<StudentDomainEvent> events = new ArrayList<>();

        @ElementCollection
        @CollectionTable(
            name = "student_submission_records",
            joinColumns = @JoinColumn(name = "student_id")
        )
        @Builder.Default
        private final Set<SubmissionRecord> submissions = new HashSet<>();

        protected Student() {
            super();
        }

        @Override
        public LocalDateTime getCreatedDate() {
            // Implement the method without using super
            return null; // Replace with actual implementation
        }

        @Override
        public LocalDateTime getLastModifiedDate() {
            // Implement the method without using super
            return null; // Replace with actual implementation
        }

        @Override
        public List<Object> getDomainEvents() {
            return List.copyOf(events);
        }

        @Override
        public void clearDomainEvents() {
            events.clear();
        }

        private void registerEvent(StudentDomainEvent event) {
            Objects.requireNonNull(event, "Domain event cannot be null");
            events.add(event);
        }

        @Override
        public void activate() {
            if (status != ActivationStatus.PENDING) {
                throw new IllegalStateException("Can only activate pending students");
            }
            this.status = ActivationStatus.ACTIVE;
            registerEvent(new StudentDomainEvent.Activated(
                Student.getId(),
                this.studentId,
                Instant.now()
            ));
        }

        @Override
        public void deactivate() {
            if (status != ActivationStatus.ACTIVE) {
                throw new IllegalStateException("Can only deactivate active students");
            }
            this.status = ActivationStatus.INACTIVE;
            registerEvent(new StudentDomainEvent.Deactivated(
                Student.getId(),
                this.studentId,
                Instant.now()
            ));
        }

        public void assignToTeam(Team team) {
            Objects.requireNonNull(team, "Team cannot be null");
            Team oldTeam = this.team;
            this.team = team;
            registerEvent(new StudentDomainEvent.TeamAssigned(
                Student.getId(),
                this.studentId,
                oldTeam != null ? oldTeam.getId() : null,
                team.getId(),
                Instant.now()
            ));
        }

        public void updateDetails(@NonNls @NonNls String firstName, @NonNls @NonNls String lastName, @NonNls @NonNls String middleName, @NonNls @NonNls String email) {
            Objects.requireNonNull(firstName, "First name cannot be null");
            Objects.requireNonNull(lastName, "Last name cannot be null");
            Objects.requireNonNull(middleName, "Middle name cannot be null");
            Objects.requireNonNull(email, "Email cannot be null");

            boolean changed = false;
            String oldEmail = this.email;

            if (!firstName.equals(this.firstName)) {
                this.firstName = firstName.trim();
                changed = true;
            }
            if (!lastName.equals(this.lastName)) {
                this.lastName = lastName.trim();
                changed = true;
            }
            if (!middleName.equals(this.middleName)) {
                this.middleName = middleName.trim();
                changed = true;
            }
            if (!email.equals(this.email)) {
                this.email = email.trim();
                registerEvent(new StudentDomainEvent.EmailChanged(
                    Student.getId(),
                    this.studentId,
                    oldEmail,
                    this.email,
                    Instant.now()
                ));
            }

            if (changed) {
                registerEvent(new StudentDomainEvent.DetailsUpdated(
                    Student.getId(),
                    this.studentId,
                    this.firstName,
                    this.lastName,
                    Instant.now()
                ));
            }
        }

        public void updateContactInfo(@NonNls String phoneNumber, @NonNls String emergencyContact) {
            Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
            Objects.requireNonNull(emergencyContact, "Emergency contact cannot be null");

            this.phoneNumber = phoneNumber.trim();
            this.emergencyContact = emergencyContact.trim();

            registerEvent(new StudentDomainEvent.ContactUpdated(
                Student.getId(),
                this.studentId,
                this.phoneNumber,
                this.emergencyContact,
                Instant.now()
            ));
        }

        public boolean isActive() {
            return status == ActivationStatus.ACTIVE;
        }

        public void trackSubmission(UUID submissionId, LocalDateTime submittedAt) {
            submissions.add(new SubmissionRecord(submissionId, submittedAt));
        }

        public Set<SubmissionRecord> getSubmissions() {
            return Collections.unmodifiableSet(submissions);
        }

        @PrePersist
        protected void onPrePersist() {
            if (studentId == null) {
                studentId = UUID.randomUUID();
            }
        }

        // This method should be provided by BaseEntity, but adding it here for completeness
        public static Long getId() {
            // Implement if not provided by BaseEntity
            return null; // Replace with actual implementation
        }

        @Embeddable
        public static class SubmissionRecord {
            private UUID submissionId;
            private LocalDateTime submittedAt;

            public SubmissionRecord() {
                // Required by JPA
            }

            public SubmissionRecord(UUID submissionId, LocalDateTime submittedAt) {
                this.submissionId = submissionId;
                this.submittedAt = submittedAt;
            }

            // Getters
            public UUID getSubmissionId() {
                return submissionId;
            }

            public LocalDateTime getSubmittedAt() {
                return submittedAt;
            }
        }
    }
