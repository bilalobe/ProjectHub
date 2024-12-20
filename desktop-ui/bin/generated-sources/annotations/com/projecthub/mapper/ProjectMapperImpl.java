package com.projecthub.mapper;

import com.projecthub.dto.ProjectDTO;
import com.projecthub.model.Project;
import com.projecthub.model.Team;

import java.util.UUID;
import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor",
        date = "2024-12-08T14:18:24+0100",
        comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.40.0.z20241112-1021, environment: Java 17.0.13 (Eclipse Adoptium)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Override
    public Project toProject(ProjectDTO projectDTO) {
        if (projectDTO == null) {
            return null;
        }

        Project project = new Project();

        project.setTeam(projectDTOToTeam(projectDTO));
        project.setId(projectDTO.getId());
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setDeadline(projectDTO.getDeadline());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setStatus(projectDTO.getStatus());

        return project;
    }

    @Override
    public ProjectDTO toProjectDTO(Project project) {
        if (project == null) {
            return null;
        }

        ProjectDTO projectDTO = new ProjectDTO();

        projectDTO.setTeamId(projectTeamId(project));
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        projectDTO.setDescription(project.getDescription());
        projectDTO.setDeadline(project.getDeadline());
        projectDTO.setStartDate(project.getStartDate());
        projectDTO.setEndDate(project.getEndDate());
        projectDTO.setStatus(project.getStatus());

        return projectDTO;
    }

    @Override
    public void updateProjectFromDTO(ProjectDTO projectDTO, Project project) {
        if (projectDTO == null) {
            return;
        }

        if (project.getTeam() == null) {
            project.setTeam(new Team());
        }
        projectDTOToTeam1(projectDTO, project.getTeam());
        project.setId(projectDTO.getId());
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setDeadline(projectDTO.getDeadline());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setStatus(projectDTO.getStatus());
    }

    protected Team projectDTOToTeam(ProjectDTO projectDTO) {
        if (projectDTO == null) {
            return null;
        }

        Team team = new Team();

        team.setId(projectDTO.getTeamId());

        return team;
    }

    private UUID projectTeamId(Project project) {
        Team team = project.getTeam();
        if (team == null) {
            return null;
        }
        return team.getId();
    }

    protected void projectDTOToTeam1(ProjectDTO projectDTO, Team mappingTarget) {
        if (projectDTO == null) {
            return;
        }

        mappingTarget.setId(projectDTO.getTeamId());
    }
}
