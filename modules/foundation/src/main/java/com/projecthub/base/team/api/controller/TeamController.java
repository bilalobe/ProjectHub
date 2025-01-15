package com.projecthub.base.team.api.controller;

import com.projecthub.base.team.api.dto.TeamDTO;
import com.projecthub.base.team.api.rest.TeamApi;
import com.projecthub.base.team.application.service.TeamService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController implements TeamApi {

    private static final Logger logger = LoggerFactory.getLogger(TeamController.class);
    private final TeamService teamService;

    public TeamController(final TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        TeamController.logger.info("Retrieving all teams");
        return ResponseEntity.ok(this.teamService.getAllTeams());
    }

    @Override
    public ResponseEntity<TeamDTO> getById(final UUID id) {
        TeamController.logger.info("Retrieving team with ID {}", id);
        return ResponseEntity.ok(this.teamService.getTeamById(id));
    }

    @Override
    public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody final TeamDTO team) {
        TeamController.logger.info("Creating new team");
        return ResponseEntity.ok(this.teamService.createTeam(team));
    }

    public ResponseEntity<TeamDTO> updateTeam(final UUID id, @Valid @RequestBody final TeamDTO team) {
        TeamController.logger.info("Updating team with ID {}", id);
        return ResponseEntity.ok(this.teamService.updateTeam(id, team));
    }

    @Override
    public ResponseEntity<Void> deleteById(final UUID id) {
        TeamController.logger.info("Deleting team with ID {}", id);
        this.teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity addUserToTeam(final UUID teamId, final UUID userId) {
        TeamController.logger.info("Adding user with ID {} to team with ID {}", userId, teamId);
        this.teamService.addUserToTeam(teamId, userId);
        return ResponseEntity.noContent().build();
    }
}
