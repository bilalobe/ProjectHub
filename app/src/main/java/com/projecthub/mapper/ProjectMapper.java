package com.projecthub.mapper;

import com.projecthub.dto.ProjectDTO;
import com.projecthub.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "team.id", source = "teamId")
    Project toProject(ProjectDTO projectDTO);

    @Mapping(source = "team.id", target = "teamId")
    ProjectDTO toProjectDTO(Project project);

    @Mapping(target = "team.id", source = "teamId")
    void updateProjectFromDTO(ProjectDTO projectDTO, @MappingTarget Project project);
}