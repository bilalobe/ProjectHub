package com.projecthub.base.milestone.api.controller;


import com.projecthub.base.milestone.api.dto.MilestoneDTO;
import com.projecthub.base.milestone.api.rest.MilestoneApi;
import com.projecthub.base.milestone.application.port.in.CreateMilestoneUseCase;
import com.projecthub.base.milestone.application.port.in.DeleteMilestoneUseCase;
import com.projecthub.base.milestone.application.port.in.LoadMilestoneUseCase;
import com.projecthub.base.milestone.application.port.in.UpdateMilestoneUseCase;
import com.projecthub.base.milestone.domain.command.CreateMilestoneCommand;
import com.projecthub.base.milestone.domain.command.UpdateMilestoneCommand;
import com.projecthub.base.milestone.domain.value.MilestoneValue;
import com.projecthub.base.milestone.infrastructure.mapper.MilestoneMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


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
        log.info("Getting all milestones");
        return ResponseEntity.ok(loadMilestoneUseCase.getAllMilestones());
    }
    @Override
    public ResponseEntity<MilestoneDTO> getMilestoneById(UUID id) {
        log.info("Get milestone with ID {}", id);
        return ResponseEntity.ok(loadMilestoneUseCase.getMilestoneById(id));
    }
    @Override
    public ResponseEntity<List<MilestoneDTO>> getMilestonesByProject(UUID projectId) {
        log.info("Get milestones for project with ID {}", projectId);
        return ResponseEntity.ok(loadMilestoneUseCase.getMilestonesByProject(projectId));
    }
    @Override
    public ResponseEntity<MilestoneDTO> createMilestone(@Valid @RequestBody MilestoneDTO milestone) {
        log.info("Creating a new milestone: {}", milestone);

        CreateMilestoneCommand command = CreateMilestoneCommand.builder()
            .milestoneDetails(mapper.toValue(milestone))
            .initiatorId(getInitiatorId())
            .projectId(milestone.projectId())
            .build();
        return ResponseEntity.ok(createMilestoneUseCase.createMilestone(command));
    }

    @Override
    public ResponseEntity<MilestoneDTO> updateMilestone(UUID id, @Valid @RequestBody MilestoneDTO milestone) {
        log.info("Updating milestone with id {}:", id);

        UpdateMilestoneCommand command = UpdateMilestoneCommand.builder()
            .id(id)
            .milestoneDetails(mapper.toValue(milestone))
            .initiatorId(getInitiatorId())
            .targetStatus(milestone.status())
            .build();
        return ResponseEntity.ok(updateMilestoneUseCase.updateMilestone(command));
    }

    @Override
    public ResponseEntity<Void> deleteMilestone(@PathVariable UUID id) {
        log.info("Deleting milestone with ID {}:", id);
        deleteMilestoneUseCase.deleteMilestone(id, getInitiatorId());
        return ResponseEntity.noContent().build();
    }

    private UUID getInitiatorId() {
        // You might retrieve user ID from security context
        // For the sake of example this is being hardcoded
        return UUID.randomUUID();
    }
}
