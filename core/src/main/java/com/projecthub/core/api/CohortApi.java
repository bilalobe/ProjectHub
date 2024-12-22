package com.projecthub.core.api;

import com.projecthub.core.dto.CohortDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Cohorts", description = "Cohort management API")
@RequestMapping("/api/v1/cohorts")
public interface CohortApi extends BaseApi<CohortDTO, UUID> {

    @Operation(summary = "Get all cohorts")
    @GetMapping
    ResponseEntity<List<CohortDTO>> getAllCohorts();

    @Operation(summary = "Get cohorts by school")
    @GetMapping("/school/{schoolId}")
    ResponseEntity<List<CohortDTO>> getCohortsBySchoolId(@PathVariable UUID schoolId);

    @Operation(summary = "Create a new cohort")
    @PostMapping
    ResponseEntity<CohortDTO> createCohort(@Valid @RequestBody CohortDTO cohort);

    @Operation(summary = "Update cohort")
    @PutMapping("/{id}")
    ResponseEntity<CohortDTO> updateCohort(
            @PathVariable UUID id,
            @Valid @RequestBody CohortDTO cohort);
}