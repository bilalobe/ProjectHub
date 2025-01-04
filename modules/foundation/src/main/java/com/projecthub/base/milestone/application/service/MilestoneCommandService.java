package com.projecthub.base.milestone.application.service;

import com.projecthub.base.milestone.api.dto.MilestoneDTO;
import com.projecthub.base.milestone.application.port.in.CreateMilestoneUseCase;
import com.projecthub.base.milestone.application.port.in.DeleteMilestoneUseCase;
import com.projecthub.base.milestone.application.port.in.UpdateMilestoneUseCase;
import com.projecthub.base.milestone.application.port.out.MilestoneEventPort;
import com.projecthub.base.milestone.application.port.out.MilestonePort;
import com.projecthub.base.milestone.domain.aggregate.MilestoneAggregate;
import com.projecthub.base.milestone.domain.command.CreateMilestoneCommand;
import com.projecthub.base.milestone.domain.command.DeleteMilestoneCommand;
import com.projecthub.base.milestone.domain.command.UpdateMilestoneCommand;
import com.projecthub.base.milestone.domain.entity.Milestone;
import com.projecthub.base.milestone.domain.event.MilestoneDomainEvent;
import com.projecthub.base.milestone.domain.exception.MilestoneNotFoundException;
import com.projecthub.base.milestone.domain.validation.MilestoneValidator;
import com.projecthub.base.milestone.infrastructure.mapper.MilestoneMapper;
import com.projecthub.base.project.infrastructure.repository.ProjectRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MilestoneCommandService implements CreateMilestoneUseCase, UpdateMilestoneUseCase, DeleteMilestoneUseCase {
    private final MilestonePort milestonePort;
    private final MilestoneEventPort milestoneEventPort;
    private final MilestoneValidator milestoneValidator;
    private final MilestoneMapper milestoneMapper;
    private final MilestoneQueryService milestoneQueryService;

    private final ProjectRepository projectRepository;

    @Override
    public MilestoneDTO createMilestone(CreateMilestoneCommand command) {
        log.debug("Creating milestone: {}", command);
        milestoneValidator.validateCreate(command.milestoneDetails(), command.projectId());
        MilestoneAggregate milestoneAggregate = MilestoneAggregate.create(command, milestoneEventPort);
        Milestone milestone = milestonePort.save(milestoneAggregate.getRoot());
        milestoneEventPort.publish(new MilestoneDomainEvent.Created(
            UUID.randomUUID(),
            milestone.getId(),
            command.initiatorId(),
            java.time.Instant.now()
        ));
        log.info("Milestone created with ID: {}", milestone.getId());
        return milestoneMapper.toDto(milestone);
    }

    @Override
    public MilestoneDTO updateMilestone(UpdateMilestoneCommand command) {
        log.debug("Updating milestone with id {}:", command.id());

        Milestone milestone = findActiveMilestoneById(command.id());

        milestone.update(command);
        milestoneValidator.validateUpdate(milestone);

        Milestone updatedMilestone = milestonePort.save(milestone);
        milestoneEventPort.publish(new MilestoneDomainEvent.Updated(
            UUID.randomUUID(),
            updatedMilestone.getId(),
            command.initiatorId(),
            java.time.Instant.now()
        ));

        log.info("Milestone updated with ID: {}", updatedMilestone.getId());
        return milestoneMapper.toDto(updatedMilestone);
    }

    @Override
    public void deleteMilestone(UUID id, UUID initiatorId) {
        log.debug("Deleting milestone: {}", id);
        Milestone milestone = findActiveMilestoneById(id);

        milestoneValidator.validateDelete(milestone);
        milestonePort.deleteById(milestone.getId());
        milestoneEventPort.publish(new MilestoneDomainEvent.Deleted(
            UUID.randomUUID(),
            milestone.getId(),
            initiatorId,
            java.time.Instant.now()
        ));

        log.info("Milestone with ID {} deleted.", id);
    }

    private Milestone findActiveMilestoneById(UUID id) {
        return milestonePort.findById(id)
            .orElseThrow(() -> new MilestoneNotFoundException("Milestone not found with id " + id));
    }
}
