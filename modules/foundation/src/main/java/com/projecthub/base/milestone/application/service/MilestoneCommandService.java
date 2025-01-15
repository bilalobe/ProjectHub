package com.projecthub.base.milestone.application.service;

import com.projecthub.base.milestone.api.dto.MilestoneDTO;
import com.projecthub.base.milestone.application.port.in.CreateMilestoneUseCase;
import com.projecthub.base.milestone.application.port.in.DeleteMilestoneUseCase;
import com.projecthub.base.milestone.application.port.in.UpdateMilestoneUseCase;
import com.projecthub.base.milestone.application.port.out.MilestoneEventPort;
import com.projecthub.base.milestone.application.port.out.MilestonePort;
import com.projecthub.base.milestone.domain.aggregate.MilestoneAggregate;
import com.projecthub.base.milestone.domain.command.CreateMilestoneCommand;
import com.projecthub.base.milestone.domain.command.UpdateMilestoneCommand;
import com.projecthub.base.milestone.domain.entity.Milestone;
import com.projecthub.base.milestone.domain.event.MilestoneDomainEvent;
import com.projecthub.base.milestone.domain.exception.MilestoneNotFoundException;
import com.projecthub.base.milestone.domain.validation.MilestoneValidator;
import com.projecthub.base.milestone.infrastructure.mapper.MilestoneMapper;
import com.projecthub.base.project.infrastructure.repository.ProjectRepository;
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
    public MilestoneDTO createMilestone(final CreateMilestoneCommand command) {
        MilestoneCommandService.log.debug("Creating milestone: {}", command);
        this.milestoneValidator.validateCreate(command.milestoneDetails(), command.projectId());
        final MilestoneAggregate milestoneAggregate = MilestoneAggregate.create(command, this.milestoneEventPort);
        final Milestone milestone = this.milestonePort.save(milestoneAggregate.getRoot());
        this.milestoneEventPort.publish(new MilestoneDomainEvent.Created(
            UUID.randomUUID(),
            milestone.getId(),
            command.initiatorId(),
            java.time.Instant.now()
        ));
        MilestoneCommandService.log.info("Milestone created with ID: {}", milestone.getId());
        return this.milestoneMapper.toDto(milestone);
    }

    @Override
    public MilestoneDTO updateMilestone(final UpdateMilestoneCommand command) {
        MilestoneCommandService.log.debug("Updating milestone with id {}:", command.id());

        final Milestone milestone = this.findActiveMilestoneById(command.id());

        milestone.update(command);
        this.milestoneValidator.validateUpdate(milestone);

        final Milestone updatedMilestone = this.milestonePort.save(milestone);
        this.milestoneEventPort.publish(new MilestoneDomainEvent.Updated(
            UUID.randomUUID(),
            updatedMilestone.getId(),
            command.initiatorId(),
            java.time.Instant.now()
        ));

        MilestoneCommandService.log.info("Milestone updated with ID: {}", updatedMilestone.getId());
        return this.milestoneMapper.toDto(updatedMilestone);
    }

    @Override
    public void deleteMilestone(final UUID id, final UUID initiatorId) {
        MilestoneCommandService.log.debug("Deleting milestone: {}", id);
        final Milestone milestone = this.findActiveMilestoneById(id);

        this.milestoneValidator.validateDelete(milestone);
        this.milestonePort.deleteById(milestone.getId());
        this.milestoneEventPort.publish(new MilestoneDomainEvent.Deleted(
            UUID.randomUUID(),
            milestone.getId(),
            initiatorId,
            java.time.Instant.now()
        ));

        MilestoneCommandService.log.info("Milestone with ID {} deleted.", id);
    }

    private Milestone findActiveMilestoneById(final UUID id) {
        return this.milestonePort.findById(id)
            .orElseThrow(() -> new MilestoneNotFoundException("Milestone not found with id " + id));
    }
}
