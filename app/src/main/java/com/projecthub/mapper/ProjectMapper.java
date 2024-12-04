package com.projecthub.mapper;

import com.projecthub.dto.ProjectDTO;
import com.projecthub.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {TeamMapper.class})
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(source = "teamId", target = "team.id")
    @Mapping(target = "tasks", ignore = true) // Assuming tasks are managed separately
    @Mapping(target = "createdAt", ignore = true) // Assuming createdAt is managed separately
    @Mapping(target = "updatedAt", ignore = true) // Assuming updatedAt is managed separately
    @Mapping(target = "createdBy", ignore = true) // Assuming createdBy is managed separately
    @Mapping(target = "deleted", ignore = true) // Assuming deleted is managed separately
    Project toProject(ProjectDTO projectDTO);

    @Mapping(source = "team.id", target = "teamId")
    ProjectDTO toProjectDTO(Project project);

    @Mapping(source = "teamId", target = "team.id")
    @Mapping(target = "tasks", ignore = true) // Assuming tasks are managed separately
    @Mapping(target = "createdAt", ignore = true) // Assuming createdAt is managed separately
    @Mapping(target = "updatedAt", ignore = true) // Assuming updatedAt is managed separately
    @Mapping(target = "createdBy", ignore = true) // Assuming createdBy is managed separately
    @Mapping(target = "deleted", ignore = true) // Assuming deleted is managed separately
    void updateProjectFromDTO(ProjectDTO projectDTO, @MappingTarget Project project);
}