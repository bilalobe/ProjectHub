package com.projecthub.mapper;

import com.projecthub.dto.ProjectSummary;
import com.projecthub.model.Project;
import com.projecthub.model.Team;

public class ProjectMapper {

    public static Project toProject(ProjectSummary projectSummary, Team team) {
        Project project = new Project();
        project.setId(projectSummary.getId());
        project.setName(projectSummary.getName());
        project.setDescription(projectSummary.getDescription());
        project.setTeam(team);
        project.setDeadline(projectSummary.getDeadline());
        return project;
    }

    public static ProjectSummary toProjectSummary(Project project) {
        return new ProjectSummary(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getTeam() != null ? project.getTeam().getId() : null,
            project.getDeadline()
        );
    }
}