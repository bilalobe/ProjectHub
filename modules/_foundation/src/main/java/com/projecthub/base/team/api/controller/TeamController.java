package com.projecthub.base.team.api.controller;

import com.projecthub.base.team.api.dto.TeamDTO;
import com.projecthub.base.team.api.rest.TeamApi;
import com.projecthub.base.team.application.service.TeamService;
import com.projecthub.base.team.infrastructure.security.TeamSecurityService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final TeamSecurityService securityService;

    public TeamController(final TeamService teamService, final TeamSecurityService securityService) {
        this.teamService = teamService;
        this.securityService = securityService;
    }

    @Override
    @PreAuthorize("hasAuthority('team:read')")
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        logger.info("Getting all teams");
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @Override
    @PreAuthorize("hasAuthority('team:read')")
    public ResponseEntity<TeamDTO> getById(final UUID id) {
        logger.info("Getting team by ID: {}", id);
        securityService.enforceReadPermission(id);
        return ResponseEntity.ok(teamService.getById(id));
    }

    @Override
    @PreAuthorize("hasAuthority('team:create')")
    public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody final TeamDTO team) {
        logger.info("Creating new team");
        securityService.enforceCreatePermission();
        return ResponseEntity.ok(teamService.createTeam(team));
    }

    @Override
    @PreAuthorize("hasAuthority('team:update')")
    public ResponseEntity<TeamDTO> updateTeam(final UUID id, @Valid @RequestBody final TeamDTO team) {
        logger.info("Updating team with ID: {}", id);
        securityService.enforceUpdatePermission(id);
        return ResponseEntity.ok(teamService.updateTeam(id, team));
    }

    @Override
    @PreAuthorize("hasAuthority('team:delete')")
    public ResponseEntity<Void> deleteById(final UUID id) {
        logger.info("Deleting team with ID: {}", id);
        securityService.enforceDeletePermission(id);
        teamService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAuthority('team.membership:add_member')")
    public ResponseEntity addUserToTeam(final UUID teamId, final UUID userId) {
        logger.info("Adding user {} to team {}", userId, teamId);
        securityService.enforceAddMemberPermission(teamId);
        teamService.addUserToTeam(teamId, userId);
        return ResponseEntity.ok().build();
    }
}
