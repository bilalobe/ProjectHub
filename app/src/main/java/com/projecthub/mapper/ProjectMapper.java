package com.projecthub.mapper;

import org.springframework.stereotype.Component;
import com.projecthub.dto.ProjectSummary;
import com.projecthub.model.Project;
import com.projecthub.model.Team;

@Component
public class ProjectMapper {

    public Project toProject(ProjectSummary projectSummary, Team team) {
        if (projectSummary == null) {
            return null;
        }
        Project project = new Project();
        project.setId(projectSummary.getId());
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
}