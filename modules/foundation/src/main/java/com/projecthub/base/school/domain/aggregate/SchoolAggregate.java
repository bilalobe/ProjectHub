package com.projecthub.base.school.domain.aggregate;

import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.school.application.port.in.command.CreateSchoolCommand;
import com.projecthub.base.school.application.port.in.command.UpdateSchoolCommand;
import com.projecthub.base.school.domain.entity.School;
import com.projecthub.base.school.domain.event.SchoolDomainEvent;
import com.projecthub.base.school.domain.event.SchoolEventPublisher;
import com.projecthub.base.school.domain.exception.SchoolDomainException;
import com.projecthub.base.school.domain.value.SchoolAddress;
import com.projecthub.base.school.domain.value.SchoolContact;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class SchoolAggregate {

    private static final int MAX_COHORTS = 20;

    private final School root;
    private final List<SchoolDomainEvent> events = new ArrayList<>();
    private final SchoolEventPublisher eventPublisher;
    private final UUID initiatorId;
    private final List<Cohort> cohorts;

    private SchoolAggregate(School root, SchoolEventPublisher eventPublisher, UUID initiatorId) {
        this.root = root;
        this.eventPublisher = eventPublisher;
        this.initiatorId = initiatorId;
        this.cohorts = new ArrayList<>();
    }

    public static SchoolAggregate create(CreateSchoolCommand command, SchoolEventPublisher eventPublisher) {
        SchoolAddress address = SchoolAddress.of(
            command.address().street(),
            command.address().city(),
            command.address().state(),
            command.address().postalCode(),
            command.address().country()
        );

        SchoolContact contact = SchoolContact.of(
            command.contact().email(),
            command.contact().phone(),
            command.contact().website()
        );

        School school = School.builder()
            .name(command.name())
            .address(address)
            .contact(contact)
            .build();

        // Assume command.getInitiatorId() is the correct accessor
        SchoolAggregate aggregate = new SchoolAggregate(school, eventPublisher, command.initiatorId());
        aggregate.registerCreated();
        return aggregate;
    }

    public static SchoolAggregateBuilder builder() {
        return new SchoolAggregateBuilder();
    }

    private void registerCreated() {
        registerEvent(new SchoolDomainEvent.Created(
            UUID.randomUUID(),
            root.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    public List<SchoolDomainEvent> getDomainEvents() {
        return List.copyOf(events);
    }

    public void clearDomainEvents() {
        events.clear();
    }

    public void addCohort(Cohort cohort) {
        validateCohortInvariant(cohort);
        cohorts.add(cohort);
        registerEvent(new SchoolDomainEvent.CohortAdded(
            UUID.randomUUID(),
            root.getId(),
            cohort.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    private void validateCohortInvariant(Cohort cohort) {
        if (cohort == null) {
            throw new SchoolDomainException("Cohort cannot be null");
        }
        if (cohorts.stream().anyMatch(c -> c.getName().equals(cohort.getName()))) {
            throw new SchoolDomainException("Cohort name must be unique within school");
        }
        if (cohorts.size() >= MAX_COHORTS) {
            throw new SchoolDomainException("Maximum number of cohorts reached");
        }
    }


    public void archive(String reason) {
        if (root.isArchived()) {
            throw new SchoolDomainException("School is already archived");
        }
        root.archive();
        registerEvent(new SchoolDomainEvent.Archived(
            UUID.randomUUID(),
            root.getId(),
            initiatorId,
            reason,
            Instant.now()
        ));
    }

    public void delete() {
        registerEvent(new SchoolDomainEvent.Deleted(
            UUID.randomUUID(),
            root.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    public void update() {
        registerEvent(new SchoolDomainEvent.Updated(
            UUID.randomUUID(),
            root.getId(),
            initiatorId,
            Instant.now()
        ));
    }

    public void updateSchoolDetails(UpdateSchoolCommand command) {
        validateStateForUpdate();
        String oldName = root.getName();

        root.update(command);

        if (!oldName.equals(root.getName())) {
            registerEvent(new SchoolDomainEvent.NameUpdated(
                UUID.randomUUID(),
                root.getId(),
                oldName,
                root.getName(),
                initiatorId,
                Instant.now()
            ));
        }
    }

    private void validateStateForUpdate() {
        if (root.isArchived()) {
            throw new SchoolDomainException("Cannot update archived school");
        }
    }

    public List<SchoolDomainEvent> getEvents() {
        return new ArrayList<>(events);
    }

    public School getRoot() {
        return root;
    }

    private void registerEvent(SchoolDomainEvent event) {
        events.add(event);
        eventPublisher.publish(event);
    }

    public static class SchoolAggregateBuilder {
        private School root;
        private SchoolEventPublisher eventPublisher;
        private UUID initiatorId;

        public SchoolAggregateBuilder root(School root) {
            this.root = root;
            return this;
        }

        public SchoolAggregateBuilder eventPublisher(SchoolEventPublisher eventPublisher) {
            this.eventPublisher = eventPublisher;
            return this;
        }

        public SchoolAggregateBuilder initiatorId(UUID initiatorId) {
            this.initiatorId = initiatorId;
            return this;
        }

        public SchoolAggregate build() {
            return new SchoolAggregate(root, eventPublisher, initiatorId);
        }
    }
}
