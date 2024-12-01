package com.projecthub.mapper;

import com.projecthub.dto.ComponentSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.model.Component;
import com.projecthub.model.Project;
import com.projecthub.repository.ProjectRepository;


@org.springframework.stereotype.Component
public class ComponentMapper {

    private final ProjectRepository projectRepository;

    public ComponentMapper(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Component toComponent(ComponentSummary componentSummary) {
        if (componentSummary == null) {
            return null;
        }

        Project project = projectRepository.findById(componentSummary.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + componentSummary.getProjectId()));

        Component component = new Component();
        component.setName(componentSummary.getName());
        component.setDescription(componentSummary.getDescription());
        component.setProject(project);

        return component;
    }

    public ComponentSummary toComponentSummary(Component component) {
        if (component == null) {
            return null;
        }

        Long projectId = (component.getProject() != null) ? component.getProject().getId() : null;

        return new ComponentSummary(
                component.getId(),
                component.getName(),
                component.getDescription(),
                projectId
        );
    }

    public void updateComponentFromSummary(ComponentSummary componentSummary, Component component) {
        if (componentSummary == null || component == null) {
            return;
        }

        if (componentSummary.getProjectId() != null) {
            Project project = projectRepository.findById(componentSummary.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + componentSummary.getProjectId()));
            component.setProject(project);
        }

        component.setName(componentSummary.getName());
        component.setDescription(componentSummary.getDescription());
    }
}
