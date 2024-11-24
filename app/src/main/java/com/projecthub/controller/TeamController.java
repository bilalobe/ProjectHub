package com.projecthub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecthub.dto.TeamSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.TeamService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Team API", description = "Operations pertaining to teams in ProjectHub")
@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @Operation(summary = "Get all teams")
    @GetMapping
    public ResponseEntity<List<TeamSummary>> getAllTeams() {
        List<TeamSummary> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    @Operation(summary = "Get team by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TeamSummary> getTeamById(@PathVariable Long id) {
        try {
            TeamSummary team = teamService.getTeamById(id);
            return ResponseEntity.ok(team);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @Operation(summary = "Create a new team")
    @PostMapping
    public ResponseEntity<TeamSummary> createTeam(@Valid @RequestBody TeamSummary team) {
        TeamSummary createdTeam = teamService.createTeam(team);
        return ResponseEntity.ok(createdTeam);
        }

        @Operation(summary = "Update an existing team")
        @PutMapping("/{id}")
        public ResponseEntity<TeamSummary> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamSummary teamSummary) {
        try {
            TeamSummary updatedTeam = teamService.updateTeam(teamSummary);
            return ResponseEntity.ok(updatedTeam);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
        }

        @Operation(summary = "Delete team by ID")
        @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteTeam(@PathVariable Long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.ok("Team deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Team not found");
        }
    }

    @Operation(summary = "Add user to team")
    @PostMapping("/{teamId}/users/{userId}")
    public ResponseEntity<TeamSummary> addUserToTeam(@PathVariable Long teamId, @PathVariable Long userId) {
        try {
            TeamSummary updatedTeam = teamService.addUserToTeam(teamId, userId);
            return ResponseEntity.ok(updatedTeam);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}