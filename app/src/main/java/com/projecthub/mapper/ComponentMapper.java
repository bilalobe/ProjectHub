package com.projecthub.mapper;

import com.projecthub.dto.ComponentDTO;
import com.projecthub.model.Component;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ProjectMapper.class})
public interface ComponentMapper {

    ComponentMapper INSTANCE = Mappers.getMapper(ComponentMapper.class);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(target = "deleted", ignore = true)
    Component toComponent(ComponentDTO componentDTO);

    @Mapping(source = "project.id", target = "projectId")
    ComponentDTO toComponentDTO(Component component);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(target = "deleted", ignore = true)
    void updateComponentFromDTO(ComponentDTO componentDTO, @MappingTarget Component component);
}