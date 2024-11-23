package com.projecthub.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.projecthub.dto.TeamSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.TeamService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public List<TeamSummary> getAllTeams() {
        return teamService.getAllTeams();
    }

    @GetMapping("/{id}")
    public Optional<TeamSummary> getTeamById(@PathVariable Long id) {
        return teamService.getTeamById(id);
    }

    @PostMapping
    public TeamSummary createTeam(@Valid @RequestBody TeamSummary team) {
        return teamService.createTeam(team);
    }

    @DeleteMapping("/{id}")
    public String deleteTeam(@PathVariable Long id) throws ResourceNotFoundException {
        teamService.deleteTeam(id);
        return "Team deleted successfully";
    }

    @PostMapping("/{teamId}/users/{userId}")
    public TeamSummary addUserToTeam(@PathVariable Long teamId, @PathVariable Long userId) throws ResourceNotFoundException {
        return teamService.addUserToTeam(teamId, userId);
    }
}