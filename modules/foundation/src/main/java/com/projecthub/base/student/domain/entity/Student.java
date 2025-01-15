package com.projecthub.base.student.domain.entity;

import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.shared.domain.entity.PreActivatable;
import com.projecthub.base.shared.domain.enums.status.ActivationStatus;
import com.projecthub.base.student.domain.event.StudentDomainEvent;
import com.projecthub.base.team.domain.entity.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@ToString(exclude = "team")
@EqualsAndHashCode(of = "studentId", callSuper = false)
@SuperBuilder // Experimental - handles inheritance better
public class Student extends BaseEntity implements PreActivatable {

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivationStatus status = ActivationStatus.PENDING;

    @With
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String email;

    @With
    @NotBlank(message = "Middle name is mandatory")
    @Size(max = 50)
    @Column(nullable = false)
    private String middleName;

    @With
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    private String phoneNumber;

    @NotNull
    @Column(nullable = false)
    private LocalDate enrollmentDate;

    @Builder.Default
    @Column(nullable = false, unique = true, updatable = false)
    private UUID studentId = UUID.randomUUID();

    @With
    @NotBlank(message = "Emergency contact is mandatory")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    @Column(nullable = false)
    private String emergencyContact;

    @With
    @NotBlank(message = "First name is mandatory")
    @Size(max = 50)
    @Column(nullable = false)
    private String firstName;

    @With
    @NotBlank(message = "Last name is mandatory")
    @Size(max = 50)
    @Column(nullable = false)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @NotNull(message = "Team is mandatory")
    private Team team;

    @Singular("event")
    @Builder.Default
    @Transient
    private final List<StudentDomainEvent> events = new ArrayList<>();

    // Domain methods
    @Override
    public void activate() {
        if (status != ActivationStatus.PENDING) {
            throw new IllegalStateException("Can only activate pending students");
        }
        this.status = ActivationStatus.ACTIVE;
        registerEvent(new StudentDomainEvent.Activated(this.getId(), this.studentId));
    }

    @Override
    public void deactivate() {
        if (status != ActivationStatus.ACTIVE) {
            throw new IllegalStateException("Can only deactivate active students");
        }
        this.status = ActivationStatus.INACTIVE;
        registerEvent(new StudentDomainEvent.Deactivated(this.getId(), this.studentId));
    }

    @Override
    public boolean isActive() {
        return status == ActivationStatus.ACTIVE;
    }

    public void assignToTeam(Team team) {
        Objects.requireNonNull(team, "Team cannot be null");
        Team oldTeam = this.team;
        this.team = team;
        registerEvent(new StudentDomainEvent.TeamAssigned(
            this.getId(),
            this.studentId,
            oldTeam != null ? oldTeam.getId() : null,
            team.getId()
        ));
    }

    public void updateDetails(String firstName, String lastName, String middleName, String email) {
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
                this.getId(),
                this.studentId,
                oldEmail,
                this.email
            ));
        }

        if (changed) {
            registerEvent(new StudentDomainEvent.DetailsUpdated(
                this.getId(),
                this.studentId,
                this.firstName,
                this.lastName
            ));
        }
    }

    public void updateContactInfo(String phoneNumber, String emergencyContact) {
        Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
        Objects.requireNonNull(emergencyContact, "Emergency contact cannot be null");

        this.phoneNumber = phoneNumber.trim();
        this.emergencyContact = emergencyContact.trim();

        registerEvent(new StudentDomainEvent.ContactUpdated(
            this.getId(),
            this.studentId,
            this.phoneNumber,
            this.emergencyContact
        ));
    }

    public List<StudentDomainEvent> getDomainEvents() {
        return List.copyOf(events);
    }

    public void clearDomainEvents() {
        events.clear();
    }

    private void registerEvent(StudentDomainEvent event) {
        events.add(event);
    }

    public static StudentBuilder builder() {
        return StudentBuilder.create();
    }

    // Make constructor package-private
    Student(String firstName, String lastName, String middleName,
           String email, String phoneNumber, String emergencyContact,
           Team team, LocalDate enrollmentDate, ActivationStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.emergencyContact = emergencyContact;
        this.team = team;
        this.enrollmentDate = enrollmentDate;
        this.status = status;
    }

    @PrePersist
    protected void onPrePersist() {
        if (studentId == null) {
            studentId = UUID.randomUUID();
        }
    }
}
