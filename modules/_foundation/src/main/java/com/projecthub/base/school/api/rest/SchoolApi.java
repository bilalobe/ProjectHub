package com.projecthub.base.school.api.rest;

import com.projecthub.base.school.api.dto.SchoolDTO;
import com.projecthub.base.school.domain.criteria.SchoolSearchCriteria;
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

@Tag(name = "Schools", description = "School management API")
@RequestMapping("/api/v1/schools")
public interface SchoolApi extends BaseApi<SchoolDTO, UUID> {

    @Operation(summary = "Get all schools")
    @GetMapping
    ResponseEntity<List<SchoolDTO>> getAllSchools();

    @Operation(summary = "Get school by ID")
    @GetMapping("/{id}")
    ResponseEntity<SchoolDTO> getSchoolById(@PathVariable UUID id);

    @Operation(summary = "Search schools")
    @GetMapping("/search")
    ResponseEntity<Page<SchoolDTO>> searchSchools(
        @Parameter(description = "Search criteria") @Valid SchoolSearchCriteria criteria,
        @Parameter(description = "Pagination parameters") Pageable pageable);

    @Operation(summary = "Create new school")
    @PostMapping
    ResponseEntity<SchoolDTO> createSchool(@Valid @RequestBody SchoolDTO school);

    @Operation(summary = "Update school")
    @PutMapping("/{id}")
    ResponseEntity<SchoolDTO> updateSchool(
        @PathVariable UUID id,
        @Valid @RequestBody SchoolDTO school);

    @Operation(summary = "Delete school")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSchool(@PathVariable UUID id);

    @Operation(summary = "Archive school")
    @PutMapping("/{id}/archive")
    ResponseEntity<Void> archiveSchool(@PathVariable UUID id);

    @Operation(summary = "Get active schools")
    @GetMapping("/active")
    ResponseEntity<Page<SchoolDTO>> getActiveSchools(Pageable pageable);

    @Operation(summary = "Get archived schools")
    @GetMapping("/archived")
    ResponseEntity<Page<SchoolDTO>> getArchivedSchools(Pageable pageable);
}
