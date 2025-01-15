package com.projecthub.base.school.application.service;

import com.projecthub.base.school.application.port.in.SchoolCommand;
import com.projecthub.base.school.application.port.in.command.ArchiveSchoolCommand;
import com.projecthub.base.school.application.port.in.command.CreateSchoolCommand;
import com.projecthub.base.school.application.port.in.command.DeleteSchoolCommand;
import com.projecthub.base.school.application.port.in.command.UpdateSchoolCommand;
import com.projecthub.base.school.domain.aggregate.SchoolAggregate;
import com.projecthub.base.school.domain.entity.School;
import com.projecthub.base.school.domain.event.SchoolDomainEvent;
import com.projecthub.base.school.domain.event.SchoolEventPublisher;
import com.projecthub.base.school.domain.exception.SchoolArchiveException;
import com.projecthub.base.school.domain.exception.SchoolUpdateException;
import com.projecthub.base.school.domain.repository.SchoolRepository;
import com.projecthub.base.school.domain.validation.SchoolValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SchoolCommandService implements SchoolCommand {
    private final SchoolRepository repository;
    private final SchoolEventPublisher eventPublisher;
    private final SchoolValidator validator;
    private final SchoolQueryService queryService;

    private ApplicationEventPublisher applicationEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSchoolEvent(final SchoolDomainEvent event) {
        this.eventPublisher.publish(event);
    }

    @Override
    public UUID createSchool(final CreateSchoolCommand command) {
        SchoolCommandService.log.debug("Creating school with name: {}", command.name());

        final SchoolAggregate schoolAggregate = SchoolAggregate.create(command, this.eventPublisher);

        this.validator.validateCreate(schoolAggregate.getRoot());
        final School saved = this.repository.save(schoolAggregate.getRoot());

        // Instead of direct publishing, send to Spring's event system first
        this.applicationEventPublisher.publishEvent(new SchoolDomainEvent.Created(
            UUID.randomUUID(),
            saved.getId(),
            command.initiatorId(),
            Instant.now()
        ));

        SchoolCommandService.log.info("Created school with ID: {}", saved.getId());
        return saved.getId();
    }

    @Override
    public void updateSchool(final UpdateSchoolCommand command) {
        SchoolCommandService.log.debug("Updating school: {}", command.id());

        try {
            final School school = this.queryService.findActiveSchoolById(command.id());
            this.validator.validateUpdate(school);

            school.update(command);
            final School saved = this.repository.save(school);
            this.applicationEventPublisher.publishEvent(new SchoolDomainEvent.Updated(
                UUID.randomUUID(),
                saved.getId(),
                command.initiatorId(),
                Instant.now()
            ));

            SchoolCommandService.log.info("Updated school: {}", command.id());
        } catch (final SchoolUpdateException e) {
            SchoolCommandService.log.warn("Cannot update archived school: {}", command.id());
            throw e;
        } catch (final Exception e) {
            SchoolCommandService.log.error("Error updating school: {}", command.id(), e);
            throw new SchoolUpdateException("Failed to update school: " + e.getMessage());
        }
    }

    @Override
    public void deleteSchool(final DeleteSchoolCommand command) {
        SchoolCommandService.log.debug("Deleting school: {}", command.id());

        final School school = this.queryService.findActiveSchoolById(command.id());
        this.validator.validateDelete(school);

        this.repository.delete(school);
        this.applicationEventPublisher.publishEvent(new SchoolDomainEvent.Deleted(
            UUID.randomUUID(),
            command.id(),
            command.initiatorId(),
            Instant.now()
        ));

        SchoolCommandService.log.info("Deleted school: {}", command.id());
    }

    @Override
    public void archiveSchool(final ArchiveSchoolCommand command) {
        Objects.requireNonNull(command, "Archive command cannot be null");
        SchoolCommandService.log.debug("Processing archive request for school: {}", command.id());

        try {
            final School school = this.queryService.findActiveSchoolById(command.id());
            this.validator.validateArchive(school);

            school.archive();
            final School saved = this.repository.save(school);
            this.applicationEventPublisher.publishEvent(new SchoolDomainEvent.Archived(
                UUID.randomUUID(),
                saved.getId(),
                command.initiatorId(),
                command.reason(),
                Instant.now()
            ));

            SchoolCommandService.log.info("Successfully archived school: {} with reason: {}",
                saved.getId(), command.reason());

        } catch (final Exception e) {
            SchoolCommandService.log.error("Failed to archive school: {} - {}",
                command.id(), e.getMessage());
            throw new SchoolArchiveException(
                "Could not archive school: " + command.id(), e);
        }
    }
}
