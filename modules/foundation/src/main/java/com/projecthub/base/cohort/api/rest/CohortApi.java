package com.projecthub.base.cohort.api.rest;

import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.shared.api.rest.BaseApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Cohorts", description = "Cohort management endpoints")
@RequestMapping("/api/v1/cohorts")
public interface CohortApi extends BaseApi<CohortDTO, UUID> {

    @Operation(summary = "Get cohorts with pagination")
    @GetMapping
    ResponseEntity<Page<CohortDTO>> getCohorts(
        @Parameter(description = "Page parameters") Pageable pageable);

    @Operation(summary = "Get active cohorts by school")
    @GetMapping("/school/{schoolId}/active")
    ResponseEntity<List<CohortDTO>> getActiveCohortsBySchool(
        @PathVariable UUID schoolId);

    @Operation(summary = "Archive a cohort")
    @PostMapping("/{id}/archive")
    ResponseEntity<CohortDTO> archiveCohort(
        @PathVariable UUID id,
        @RequestParam String reason);

    @Operation(summary = "Mark cohort as completed")
    @PostMapping("/{id}/complete")
    ResponseEntity<CohortDTO> completeCohort(@PathVariable UUID id);

    @Operation(summary = "Add team to cohort")
    @PostMapping("/{id}/teams")
    ResponseEntity<CohortDTO> addTeam(
        @PathVariable UUID id,
        @RequestParam UUID teamId);
}
