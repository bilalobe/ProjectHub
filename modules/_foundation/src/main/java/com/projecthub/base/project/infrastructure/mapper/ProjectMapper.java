package com.projecthub.base.project.infrastructure.mapper;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.shared.api.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = { ComponentMapper.class }
)
public interface ProjectMapper extends BaseMapper<ProjectDTO, Project> {

    @Override
    @Mapping(target = "team.id", source = "teamId")
    Project toEntity(ProjectDTO dto);

    @Override
    @Mapping(source = "team.id", target = "teamId")
    ProjectDTO toDto(Project entity);

    @Override
    @Mapping(target = "team.id", source = "teamId")
    void updateEntityFromDto(ProjectDTO dto, @MappingTarget Project entity);
}