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
    public void handleSchoolEvent(SchoolDomainEvent event) {
        eventPublisher.publish(event);
    }

    @Override
    public UUID createSchool(CreateSchoolCommand command) {
        log.debug("Creating school with name: {}", command.name());

        SchoolAggregate schoolAggregate = SchoolAggregate.create(command, eventPublisher);

        validator.validateCreate(schoolAggregate.getRoot());
        School saved = repository.save(schoolAggregate.getRoot());

        // Instead of direct publishing, send to Spring's event system first
        applicationEventPublisher.publishEvent(new SchoolDomainEvent.Created(
            UUID.randomUUID(),
            saved.getId(),
            command.initiatorId(),
            Instant.now()
        ));

        log.info("Created school with ID: {}", saved.getId());
        return saved.getId();
    }

    @Override
    public void updateSchool(UpdateSchoolCommand command) {
        log.debug("Updating school: {}", command.id());

        try {
            School school = queryService.findActiveSchoolById(command.id());
            validator.validateUpdate(school);

            school.update(command);
            School saved = repository.save(school);
            applicationEventPublisher.publishEvent(new SchoolDomainEvent.Updated(
                UUID.randomUUID(),
                saved.getId(),
                command.initiatorId(),
                Instant.now()
            ));

            log.info("Updated school: {}", command.id());
        } catch (SchoolUpdateException e) {
            log.warn("Cannot update archived school: {}", command.id());
            throw e;
        } catch (Exception e) {
            log.error("Error updating school: {}", command.id(), e);
            throw new SchoolUpdateException("Failed to update school: " + e.getMessage());
        }
    }

    @Override
    public void deleteSchool(DeleteSchoolCommand command) {
        log.debug("Deleting school: {}", command.id());

        School school = queryService.findActiveSchoolById(command.id());
        validator.validateDelete(school);

        repository.delete(school);
        applicationEventPublisher.publishEvent(new SchoolDomainEvent.Deleted(
            UUID.randomUUID(),
            command.id(),
            command.initiatorId(),
            Instant.now()
        ));

        log.info("Deleted school: {}", command.id());
    }

    @Override
    public void archiveSchool(ArchiveSchoolCommand command) {
        Objects.requireNonNull(command, "Archive command cannot be null");
        log.debug("Processing archive request for school: {}", command.id());

        try {
            School school = queryService.findActiveSchoolById(command.id());
            validator.validateArchive(school);

            school.archive();
            School saved = repository.save(school);
            applicationEventPublisher.publishEvent(new SchoolDomainEvent.Archived(
                UUID.randomUUID(),
                saved.getId(),
                command.initiatorId(),
                command.reason(),
                Instant.now()
            ));

            log.info("Successfully archived school: {} with reason: {}",
                saved.getId(), command.reason());

        } catch (Exception e) {
            log.error("Failed to archive school: {} - {}",
                command.id(), e.getMessage());
            throw new SchoolArchiveException(
                "Could not archive school: " + command.id(), e);
        }
    }
}
