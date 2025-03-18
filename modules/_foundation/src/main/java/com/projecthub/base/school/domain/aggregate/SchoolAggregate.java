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
import org.jetbrains.annotations.NonNls;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class SchoolAggregate {

    private static final int MAX_COHORTS = 20;

    @Getter
    private final School root;
    private final List<SchoolDomainEvent> events = new ArrayList<>();
    private final SchoolEventPublisher eventPublisher;
    private final UUID initiatorId;
    private final List<Cohort> cohorts;

    private SchoolAggregate(final School root, final SchoolEventPublisher eventPublisher, final UUID initiatorId) {
        this.root = root;
        this.eventPublisher = eventPublisher;
        this.initiatorId = initiatorId;
        cohorts = new ArrayList<>();
    }

    public static SchoolAggregate create(final CreateSchoolCommand command, final SchoolEventPublisher eventPublisher) {
        final SchoolAddress address = SchoolAddress.of(
            command.address().street(),
            command.address().city(),
            command.address().state(),
            command.address().postalCode(),
            command.address().country()
        );

        final SchoolContact contact = SchoolContact.of(
            command.contact().email(),
            command.contact().phone(),
            command.contact().website()
        );

        final School school = School.builder()
            .name(command.name())
            .address(address)
            .contact(contact)
            .build();

        // Assume command.getInitiatorId() is the correct accessor
        final SchoolAggregate aggregate = new SchoolAggregate(school, eventPublisher, command.initiatorId());
        aggregate.registerCreated();
        return aggregate;
    }

    public static SchoolAggregateBuilder builder() {
        return new SchoolAggregateBuilder();
    }

    private void registerCreated() {
        this.registerEvent(new SchoolDomainEvent.Created(
            UUID.randomUUID(),
            this.root.getId(),
            this.initiatorId,
            Instant.now()
        ));
    }

    public List<SchoolDomainEvent> getDomainEvents() {
        return List.copyOf(this.events);
    }

    public void clearDomainEvents() {
        this.events.clear();
    }

    public void addCohort(final Cohort cohort) {
        this.validateCohortInvariant(cohort);
        this.cohorts.add(cohort);
        this.registerEvent(new SchoolDomainEvent.CohortAdded(
            UUID.randomUUID(),
            this.root.getId(),
            cohort.getId(),
            this.initiatorId,
            Instant.now()
        ));
    }

    private void validateCohortInvariant(final Cohort cohort) {
        if (null == cohort) {
            throw new SchoolDomainException("Cohort cannot be null");
        }
        if (this.cohorts.stream().anyMatch(c -> c.getName().equals(cohort.getName()))) {
            throw new SchoolDomainException("Cohort name must be unique within school");
        }
        if (MAX_COHORTS <= cohorts.size()) {
            throw new SchoolDomainException("Maximum number of cohorts reached");
        }
    }


    public void archive(final String reason) {
        if (this.root.isArchived()) {
            throw new SchoolDomainException("School is already archived");
        }
        this.root.archive();
        this.registerEvent(new SchoolDomainEvent.Archived(
            UUID.randomUUID(),
            this.root.getId(),
            this.initiatorId,
            reason,
            Instant.now()
        ));
    }

    public void delete() {
        this.registerEvent(new SchoolDomainEvent.Deleted(
            UUID.randomUUID(),
            this.root.getId(),
            this.initiatorId,
            Instant.now()
        ));
    }

    public void update() {
        this.registerEvent(new SchoolDomainEvent.Updated(
            UUID.randomUUID(),
            this.root.getId(),
            this.initiatorId,
            Instant.now()
        ));
    }

    public void updateSchoolDetails(final UpdateSchoolCommand command) {
        this.validateStateForUpdate();
        @NonNls final String oldName = this.root.getName();

        this.root.update(command);

        if (!oldName.equals(this.root.getName())) {
            this.registerEvent(new SchoolDomainEvent.NameUpdated(
                UUID.randomUUID(),
                this.root.getId(),
                oldName,
                this.root.getName(),
                this.initiatorId,
                Instant.now()
            ));
        }
    }

    private void validateStateForUpdate() {
        if (this.root.isArchived()) {
            throw new SchoolDomainException("Cannot update archived school");
        }
    }

    public List<SchoolDomainEvent> getEvents() {
        return new ArrayList<>(this.events);
    }

    private void registerEvent(final SchoolDomainEvent event) {
        this.events.add(event);
        this.eventPublisher.publish(event);
    }

    public static class SchoolAggregateBuilder {
        private School root = null;
        private SchoolEventPublisher eventPublisher = null;
        private UUID initiatorId = null;

        public SchoolAggregateBuilder() {
        }

        public SchoolAggregateBuilder root(final School root) {
            this.root = root;
            return this;
        }

        public SchoolAggregateBuilder eventPublisher(final SchoolEventPublisher eventPublisher) {
            this.eventPublisher = eventPublisher;
            return this;
        }

        public SchoolAggregateBuilder initiatorId(final UUID initiatorId) {
            this.initiatorId = initiatorId;
            return this;
        }

        public SchoolAggregate build() {
            return new SchoolAggregate(this.root, this.eventPublisher, this.initiatorId);
        }
    }
}
