package com.projecthub.core.api;

import com.projecthub.core.dto.ProjectDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Projects", description = "Project management API")
@RequestMapping("/api/v1/projects")
public interface ProjectApi extends BaseApi<ProjectDTO, UUID> {

    @Operation(summary = "Get all projects",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved projects")
            })
    @GetMapping
    ResponseEntity<List<ProjectDTO>> getAllProjects();

    @Operation(summary = "Create a new project",
            responses = {
                @ApiResponse(responseCode = "201", description = "Project created successfully")
            })
    @PostMapping
    ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO project);

    @Operation(summary = "Update project",
            responses = {
                @ApiResponse(responseCode = "200", description = "Project updated successfully")
            })
    @PutMapping("/{id}")
    ResponseEntity<ProjectDTO> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody ProjectDTO project);
}
