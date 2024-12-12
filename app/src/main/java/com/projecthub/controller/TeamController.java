package com.projecthub.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecthub.dto.TeamDTO;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.TeamService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "Team API", description = "Operations pertaining to teams in ProjectHub")
@RestController
@RequestMapping("/teams")
public class TeamController {

    private static final Logger logger = LoggerFactory.getLogger(TeamController.class);

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @Operation(summary = "Get all teams")
    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        List<TeamDTO> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    @Operation(summary = "Get team by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable UUID id) {
        logger.info("Retrieving team with ID {}", id);
        TeamDTO team = teamService.getTeamById(id);
        return ResponseEntity.ok(team);
    }

    @Operation(summary = "Create a new team")
    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody TeamDTO team) {
        TeamDTO createdTeam = teamService.createTeam(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeam);
    }

    @Operation(summary = "Update an existing team")
    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> updateTeam(@PathVariable UUID id, @Valid @RequestBody TeamDTO teamSummary) {
        TeamDTO updatedTeam = teamService.updateTeam(id, teamSummary);
        return ResponseEntity.ok(updatedTeam);
    }

    @Operation(summary = "Delete team by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add user to team")
    @PostMapping("/{teamId}/users/{userId}")
    public ResponseEntity<TeamDTO> addUserToTeam(@PathVariable UUID teamId, @PathVariable UUID userId) {
        TeamDTO updatedTeam = teamService.addUserToTeam(teamId, userId);
        return ResponseEntity.ok(updatedTeam);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Resource not found", ex);
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}