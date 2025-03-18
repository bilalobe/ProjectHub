package com.projecthub.base.milestone.api.controller;


import com.projecthub.base.milestone.api.dto.MilestoneDTO;
import com.projecthub.base.milestone.api.rest.MilestoneApi;
import com.projecthub.base.milestone.application.port.in.CreateMilestoneUseCase;
import com.projecthub.base.milestone.application.port.in.DeleteMilestoneUseCase;
import com.projecthub.base.milestone.application.port.in.LoadMilestoneUseCase;
import com.projecthub.base.milestone.application.port.in.UpdateMilestoneUseCase;
import com.projecthub.base.milestone.domain.command.CreateMilestoneCommand;
import com.projecthub.base.milestone.domain.command.UpdateMilestoneCommand;
import com.projecthub.base.milestone.infrastructure.mapper.MilestoneMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/milestones")
@RequiredArgsConstructor
@Slf4j
public class MilestoneController implements MilestoneApi {
    private final CreateMilestoneUseCase createMilestoneUseCase;
    private final UpdateMilestoneUseCase updateMilestoneUseCase;
    private final DeleteMilestoneUseCase deleteMilestoneUseCase;
    private final LoadMilestoneUseCase loadMilestoneUseCase;
    private final MilestoneMapper mapper;


    @Override
    public ResponseEntity<List<MilestoneDTO>> getAllMilestones() {
        MilestoneController.log.info("Getting all milestones");
        return ResponseEntity.ok(this.loadMilestoneUseCase.getAllMilestones());
    }

    @Override
    public ResponseEntity<MilestoneDTO> getMilestoneById(final UUID id) {
        MilestoneController.log.info("Get milestone with ID {}", id);
        return ResponseEntity.ok(this.loadMilestoneUseCase.getMilestoneById(id));
    }

    @Override
    public ResponseEntity<List<MilestoneDTO>> getMilestonesByProject(final UUID projectId) {
        MilestoneController.log.info("Get milestones for project with ID {}", projectId);
        return ResponseEntity.ok(this.loadMilestoneUseCase.getMilestonesByProject(projectId));
    }

    @Override
    public ResponseEntity<MilestoneDTO> createMilestone(@Valid @RequestBody final MilestoneDTO milestone) {
        MilestoneController.log.info("Creating a new milestone: {}", milestone);

        final CreateMilestoneCommand command = CreateMilestoneCommand.builder()
            .milestoneDetails(this.mapper.toValue(milestone))
            .initiatorId(MilestoneController.getInitiatorId())
            .projectId(milestone.projectId())
            .build();
        return ResponseEntity.ok(this.createMilestoneUseCase.createMilestone(command));
    }

    @Override
    public ResponseEntity<MilestoneDTO> updateMilestone(final UUID id, @Valid @RequestBody final MilestoneDTO milestone) {
        MilestoneController.log.info("Updating milestone with id {}:", id);

        final UpdateMilestoneCommand command = UpdateMilestoneCommand.builder()
            .id(id)
            .milestoneDetails(this.mapper.toValue(milestone))
            .initiatorId(MilestoneController.getInitiatorId())
            .targetStatus(milestone.status())
            .build();
        return ResponseEntity.ok(this.updateMilestoneUseCase.updateMilestone(command));
    }

    @Override
    public ResponseEntity<Void> deleteMilestone(@PathVariable final UUID id) {
        MilestoneController.log.info("Deleting milestone with ID {}:", id);
        this.deleteMilestoneUseCase.deleteMilestone(id, MilestoneController.getInitiatorId());
        return ResponseEntity.noContent().build();
    }

    private static UUID getInitiatorId() {
        // You might retrieve user ID from security context
        // For the sake of example this is being hardcoded
        return UUID.randomUUID();
    }
}
