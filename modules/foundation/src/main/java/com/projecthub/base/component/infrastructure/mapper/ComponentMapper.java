package com.projecthub.base.component.infrastructure.mapper;


import com.projecthub.base.component.api.dto.ComponentDTO;
import com.projecthub.base.component.domain.entity.Component;
import com.projecthub.base.shared.api.mapper.BaseMapper;
import org.mapstruct.*;

/**
 * Mapper for Component entity with protected relationship handling.
 */
@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ComponentMapper extends BaseMapper<ComponentDTO, Component> {

    @Override
    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "id", ignore = true)
    Component toEntity(ComponentDTO dto);

    @Override
    @Mapping(source = "project.id", target = "projectId")
    ComponentDTO toDto(Component entity);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ComponentDTO dto, @MappingTarget Component entity);
}
