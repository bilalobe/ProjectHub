package com.projecthub.base.project.api.controller;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.api.rest.ProjectApi;
import com.projecthub.base.project.application.facade.ProjectFacade;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.project.domain.template.ProjectTemplate;
import com.projecthub.base.project.infrastructure.service.ProjectSearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Project management endpoints")
@RequiredArgsConstructor
public class ProjectController implements ProjectApi {

    private final ProjectFacade projectFacade;

    @Override
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        log.info("Retrieving all projects");
        return ResponseEntity.ok(projectFacade.getAllProjects());
    }

    @Override
    public ResponseEntity<ProjectDTO> getById(@PathVariable UUID id) {
        log.info("Retrieving project with ID {}", id);
        return ResponseEntity.ok(projectFacade.getProjectById(id));
    }

    @Override
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO project) {
        log.info("Creating new project: {}", project.name());
        return ResponseEntity.ok(projectFacade.createProject(project));
    }

    @Override
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable UUID id, @Valid @RequestBody ProjectDTO project) {
        log.info("Updating project with ID {}: {}", id, project.name());
        return ResponseEntity.ok(projectFacade.updateProject(id, project));
    }

    @Override
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        log.info("Deleting project with ID {}", id);
        projectFacade.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update project status")
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id, @RequestParam ProjectStatus status) {
        log.info("Updating status of project {} to {}", id, status);
        projectFacade.updateProjectStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get projects by team")
    @GetMapping("/team/{teamId}")
    public ResponseEntity<Page<ProjectDTO>> getProjectsByTeam(@PathVariable UUID teamId, Pageable pageable) {
        log.info("Retrieving projects for team {}", teamId);
        return ResponseEntity.ok(projectFacade.getProjectsByTeam(teamId, pageable));
    }

    @Operation(summary = "Search projects")
    @GetMapping("/search")
    public ResponseEntity<List<ProjectDTO>> searchProjects(@RequestParam String query) {
        log.info("Searching projects with query: {}", query);
        return ResponseEntity.ok(projectFacade.searchProjects(query));
    }

    @Operation(summary = "Advanced project search")
    @PostMapping("/search")
    public ResponseEntity<Page<ProjectDTO>> searchProjects(
        @RequestBody ProjectSearchService.ProjectSearchCriteria criteria,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("Performing advanced project search with criteria: {}", criteria);
        return ResponseEntity.ok(projectFacade.searchProjects(criteria, pageable));
    }

    @Operation(summary = "Get available project templates")
    @GetMapping("/templates")
    public ResponseEntity<List<ProjectTemplate>> getProjectTemplates() {
        log.info("Retrieving available project templates");
        return ResponseEntity.ok(projectFacade.getProjectTemplates());
    }

    @Operation(summary = "Create project from template")
    @PostMapping("/templates/{templateId}")
    public ResponseEntity<ProjectDTO> createFromTemplate(
        @PathVariable UUID templateId,
        @RequestParam UUID teamId,
        @RequestParam String projectName
    ) {
        log.info("Creating project from template {} for team {}", templateId, teamId);
        return ResponseEntity.ok(projectFacade.createProjectFromTemplate(templateId, teamId, projectName));
    }

    @Operation(summary = "Get project statistics")
    @GetMapping("/statistics")
    public ResponseEntity<ProjectStatistics> getProjectStatistics(
        @RequestParam(required = false) UUID teamId
    ) {
        log.info("Retrieving project statistics for team {}", teamId);
        return ResponseEntity.ok(projectFacade.getProjectStatistics(teamId));
    }

    @Operation(summary = "Export projects to Excel")
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel(
        @RequestParam(required = false) UUID teamId
    ) {
        log.info("Exporting projects to Excel for team {}", teamId);
        try {
            byte[] excelFile = projectFacade.exportProjectsToExcel(teamId);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=projects.xlsx")
                .header(HttpHeaders.CONTENT_TYPE, 
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excelFile);
        } catch (IOException e) {
            log.error("Error exporting projects to Excel", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Export detailed project data")
    @GetMapping("/{id}/export")
    public ResponseEntity<ProjectExportDTO> exportProjectDetails(
        @PathVariable UUID id
    ) {
        log.info("Exporting detailed data for project {}", id);
        return ResponseEntity.ok(projectFacade.exportProjectDetails(id));
    }

    @Operation(summary = "Get project audit trail")
    @GetMapping("/{id}/audit")
    public ResponseEntity<List<AuditEntry>> getProjectAuditTrail(
        @PathVariable UUID id
    ) {
        log.info("Retrieving audit trail for project {}", id);
        return ResponseEntity.ok(projectFacade.getProjectAuditTrail(id));
    }

    @Operation(summary = "Get team projects audit trail")
    @GetMapping("/team/{teamId}/audit")
    public ResponseEntity<List<AuditEntry>> getTeamProjectsAuditTrail(
        @PathVariable UUID teamId
    ) {
        log.info("Retrieving audit trail for team projects {}", teamId);
        return ResponseEntity.ok(projectFacade.getTeamProjectsAuditTrail(teamId));
    }

}
