package com.projecthub.mapper;

import org.springframework.stereotype.Component;

import com.projecthub.dto.ProjectSummary;
import com.projecthub.model.Project;
import com.projecthub.model.Team;

@Component
public class ProjectMapper {

    public Project toProject(ProjectSummary projectSummary, Team team) {
        if (projectSummary == null || team == null) {
            return null;
        }
        Project project = new Project();
        project.setId(projectSummary.getId());
        project.setName(projectSummary.getName());
        project.setDescription(projectSummary.getDescription());
        project.setTeam(team);
        project.setDeadline(projectSummary.getDeadline());
        return project;
    }

    public ProjectSummary toProjectSummary(Project project) {
        if (project == null) {
            return null;
        }
        return new ProjectSummary(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getTeam() != null ? project.getTeam().getId() : null,
            project.getDeadline()
        );
    }
}