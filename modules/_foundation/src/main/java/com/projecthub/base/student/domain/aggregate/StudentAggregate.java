package com.projecthub.base.student.domain.aggregate;

import com.projecthub.base.shared.domain.enums.status.ActivationStatus;
import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.student.domain.event.StudentDomainEvent;
import com.projecthub.base.team.domain.entity.Team;
import lombok.Getter;
import org.jetbrains.annotations.NonNls;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@AggregateRoot
public class StudentAggregate {
    private final Student root;
    private final List<StudentDomainEvent> events;
    private final UUID initiatorId;

    private StudentAggregate(Student root, UUID initiatorId) {
        this.root = root;
        this.initiatorId = initiatorId;
        this.events = new ArrayList<>();
    }

    public static StudentAggregate reconstitute(Student student, UUID initiatorId) {
        return new StudentAggregate(student, initiatorId);
    }

    public void activate() {
        if (!ActivationStatus.PENDING.equals(root.getStatus())) {
            throw new IllegalStateException("Can only activate pending students");
        }
        root.setStatus(ActivationStatus.ACTIVE);
        registerEvent(new StudentDomainEvent.Activated(
            UUID.randomUUID(),
            root.getStudentId(),
            initiatorId,
            Instant.now()
        ));
    }

    public void deactivate() {
        if (!root.getStatus().equals(ActivationStatus.ACTIVE)) {
            throw new IllegalStateException("Can only deactivate active students");
        }
        root.setStatus(ActivationStatus.INACTIVE);
        registerEvent(new StudentDomainEvent.Deactivated(
            UUID.randomUUID(),
            root.getStudentId(),
            initiatorId,
            Instant.now()
        ));
    }

    public void assignToTeam(Team team) {
        Objects.requireNonNull(team, "Team cannot be null");
        Team oldTeam = root.getTeam();
        UUID oldTeamId = oldTeam != null ? oldTeam.getTeamId() : null;

        root.setTeam(team);
        registerEvent(new StudentDomainEvent.TeamAssigned(
            UUID.randomUUID(),
            root.getStudentId(),
            oldTeamId,
            team.getTeamId(),
            initiatorId,
            Instant.now()
        ));
    }

    public void updateDetails(@NonNls @NonNls String firstName, @NonNls @NonNls String lastName, @NonNls @NonNls String middleName, @NonNls @NonNls String email) {
        Objects.requireNonNull(firstName, "First name cannot be null");
        Objects.requireNonNull(lastName, "Last name cannot be null");
        Objects.requireNonNull(middleName, "Middle name cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");

        String oldEmail = root.getEmail();

        boolean detailsChanged = !firstName.equals(root.getFirstName()) ||
                               !lastName.equals(root.getLastName()) ||
                               !middleName.equals(root.getMiddleName());

        root.setFirstName(firstName.trim());
        root.setLastName(lastName.trim());
        root.setMiddleName(middleName.trim());
        root.setEmail(email.trim());

        if (!email.equals(oldEmail)) {
            registerEvent(new StudentDomainEvent.EmailChanged(
                UUID.randomUUID(),
                root.getStudentId(),
                oldEmail,
                email,
                initiatorId,
                Instant.now()
            ));
        }

        if (detailsChanged) {
            registerEvent(new StudentDomainEvent.DetailsUpdated(
                UUID.randomUUID(),
                root.getStudentId(),
                firstName,
                lastName,
                middleName,
                initiatorId,
                Instant.now()
            ));
        }
    }

    public void updateContactInfo(@NonNls String phoneNumber, @NonNls String emergencyContact) {
        Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
        Objects.requireNonNull(emergencyContact, "Emergency contact cannot be null");

        root.setPhoneNumber(phoneNumber.trim());
        root.setEmergencyContact(emergencyContact.trim());

        registerEvent(new StudentDomainEvent.ContactUpdated(
            UUID.randomUUID(),
            root.getStudentId(),
            phoneNumber,
            emergencyContact,
            initiatorId,
            Instant.now()
        ));
    }

    public void trackSubmission(UUID submissionId, UUID projectId, Instant submittedAt) {
        Objects.requireNonNull(submissionId, "Submission ID cannot be null");
        Objects.requireNonNull(submittedAt, "Submission timestamp cannot be null");

        root.trackSubmission(submissionId, LocalDateTime.ofInstant(submittedAt, ZoneId.systemDefault()));
        registerEvent(new StudentDomainEvent.SubmissionTracked(
            UUID.randomUUID(),
            root.getStudentId(),
            submissionId,
            projectId,
            submittedAt,
            initiatorId,
            Instant.now()
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
}
