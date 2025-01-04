package com.projecthub.base.milestone.api.rest;

import com.projecthub.base.milestone.api.dto.MilestoneDTO;
import com.projecthub.base.shared.api.rest.BaseApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Tag(name = "Milestones", description = "Milestone management API")
@RequestMapping("/api/v1/milestones")
public interface MilestoneApi extends BaseApi<MilestoneDTO, UUID> {

    @Operation(summary = "Get all milestones")
    @GetMapping
    ResponseEntity<List<MilestoneDTO>> getAllMilestones();

    @Operation(summary = "Get milestone by ID")
    @GetMapping("/{id}")
    ResponseEntity<MilestoneDTO> getMilestoneById(@PathVariable UUID id);

    @Operation(summary = "Search milestones by project")
    @GetMapping("/project/{projectId}")
    ResponseEntity<List<MilestoneDTO>> getMilestonesByProject(@PathVariable UUID projectId);

    @Operation(summary = "Create new milestone")
    @PostMapping
    ResponseEntity<MilestoneDTO> createMilestone(@Valid @RequestBody MilestoneDTO milestone);

    @Operation(summary = "Update milestone")
    @PutMapping("/{id}")
    ResponseEntity<MilestoneDTO> updateMilestone(
        @PathVariable UUID id,
        @Valid @RequestBody MilestoneDTO milestone);

    @Operation(summary = "Delete milestone")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteMilestone(@PathVariable UUID id);

}
