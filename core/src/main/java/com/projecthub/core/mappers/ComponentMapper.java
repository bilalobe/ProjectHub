package com.projecthub.core.mappers;

import com.projecthub.core.dto.ComponentDTO;
import com.projecthub.core.models.Component;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ComponentMapper {

    @Mapping(target = "project.id", source = "projectId")
    Component toComponent(ComponentDTO componentDTO);

    @Mapping(source = "project.id", target = "projectId")
    ComponentDTO toComponentDTO(Component component);

    @Mapping(target = "project.id", source = "projectId")
    void updateComponentFromDTO(ComponentDTO componentDTO, @MappingTarget Component component);
}