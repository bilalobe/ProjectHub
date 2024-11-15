package com.projecthub.controller;

import com.projecthub.dto.TeamSummary;
import com.projecthub.model.Team;
import com.projecthub.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping
    public List<TeamSummary> getAllTeams() {
        return teamService.getAllTeams().stream()
                .map(TeamSummary::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public String createTeam(@Valid @RequestBody Team team) {
        teamService.saveTeam(team);
        return "Team created successfully";
    }

    @DeleteMapping("/{id}")
    public String deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return "Team deleted successfully";
    }
}