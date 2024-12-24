package com.projecthub.core.mappers;

import com.projecthub.core.dto.ProjectDTO;
import com.projecthub.core.models.Project;
import org.mapstruct.*;

/**
 * Mapper for Project entity with protected relationship handling.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper extends BaseMapper<ProjectDTO, Project> {

    @Override
    @Mapping(target = "team.id", source = "teamId")
    @Mapping(target = "id", ignore = true)
    Project toEntity(ProjectDTO dto);

    @Override
    @Mapping(source = "team.id", target = "teamId")
    ProjectDTO toDto(Project entity);

    @Override
    @Mapping(target = "team.id", source = "teamId")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ProjectDTO dto, @MappingTarget Project entity);

    @Named("summary")
    @Mapping(source = "team.id", target = "teamId")
    ProjectDTO toSummaryDto(Project entity);
}