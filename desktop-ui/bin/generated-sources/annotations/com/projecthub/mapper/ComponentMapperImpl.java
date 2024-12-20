package com.projecthub.mapper;

import com.projecthub.dto.ComponentDTO;
import com.projecthub.model.Component;
import com.projecthub.model.Project;

import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor",
        date = "2024-12-08T14:18:24+0100",
        comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.40.0.z20241112-1021, environment: Java 17.0.13 (Eclipse Adoptium)"
)
@org.springframework.stereotype.Component
public class ComponentMapperImpl implements ComponentMapper {

    @Override
    public Component toComponent(ComponentDTO componentDTO) {
        if (componentDTO == null) {
            return null;
        }

        Component component = new Component();

        component.setProject(componentDTOToProject(componentDTO));
        component.setId(componentDTO.getId());
        component.setName(componentDTO.getName());
        component.setDescription(componentDTO.getDescription());

        return component;
    }

    @Override
    public ComponentDTO toComponentDTO(Component component) {
        if (component == null) {
            return null;
        }

        ComponentDTO componentDTO = new ComponentDTO();

        componentDTO.setProjectId(componentProjectId(component));
        componentDTO.setId(component.getId());
        componentDTO.setName(component.getName());
        componentDTO.setDescription(component.getDescription());

        return componentDTO;
    }

    @Override
    public void updateComponentFromDTO(ComponentDTO componentDTO, Component component) {
        if (componentDTO == null) {
            return;
        }

        if (component.getProject() == null) {
            component.setProject(new Project());
        }
        componentDTOToProject1(componentDTO, component.getProject());
        component.setId(componentDTO.getId());
        component.setName(componentDTO.getName());
        component.setDescription(componentDTO.getDescription());
    }

    protected Project componentDTOToProject(ComponentDTO componentDTO) {
        if (componentDTO == null) {
            return null;
        }

        Project project = new Project();

        project.setId(componentDTO.getProjectId());

        return project;
    }

    private UUID componentProjectId(Component component) {
        Project project = component.getProject();
        if (project == null) {
            return null;
        }
        return project.getId();
    }

    protected void componentDTOToProject1(ComponentDTO componentDTO, Project mappingTarget) {
        if (componentDTO == null) {
            return;
        }

        mappingTarget.setId(componentDTO.getProjectId());
    }
}
