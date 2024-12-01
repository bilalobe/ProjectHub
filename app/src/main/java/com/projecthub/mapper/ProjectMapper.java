package com.projecthub.mapper;

import com.projecthub.dto.ProjectSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.model.Project;
import com.projecthub.model.Team;
import com.projecthub.repository.TeamRepository;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    private final TeamRepository teamRepository;

    public ProjectMapper(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Project toProject(ProjectSummary projectSummary) {
        if (projectSummary == null) {
            return null;
        }

        Team team = teamRepository.findById(projectSummary.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + projectSummary.getTeamId()));

        Project project = new Project();
        project.setName(projectSummary.getName());
        project.setDescription(projectSummary.getDescription());
        project.setDeadline(projectSummary.getDeadline());
        project.setStartDate(projectSummary.getStartDate());
        project.setEndDate(projectSummary.getEndDate());
        project.setStatus(projectSummary.getStatus());
        project.setTeam(team);

        return project;
    }

    public ProjectSummary toProjectSummary(Project project) {
        if (project == null) {
            return null;
        }

        Long teamId = (project.getTeam() != null) ? project.getTeam().getId() : null;

        return new ProjectSummary(
                project.getId(),
                project.getName(),
                project.getDescription(),
                teamId,
                project.getDeadline(),
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus()
        );
    }

    public void updateProjectFromSummary(ProjectSummary projectSummary, Project project) {
        if (projectSummary == null || project == null) {
            return;
        }

        if (projectSummary.getTeamId() != null) {
            Team team = teamRepository.findById(projectSummary.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + projectSummary.getTeamId()));
            project.setTeam(team);
        }

        project.setName(projectSummary.getName());
        project.setDescription(projectSummary.getDescription());
        project.setDeadline(projectSummary.getDeadline());
        project.setStartDate(projectSummary.getStartDate());
        project.setEndDate(projectSummary.getEndDate());
        project.setStatus(projectSummary.getStatus());
    }
}