package com.projecthub.base.project.domain.service;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.template.ProjectTemplate;
import com.projecthub.base.project.infrastructure.mapper.ProjectMapper;
import com.projecthub.base.project.infrastructure.repository.ProjectRepository;
import com.projecthub.base.project.infrastructure.repository.ProjectTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectTemplateService {
    
    private final ProjectTemplateRepository templateRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public List<ProjectTemplate> getActiveTemplates() {
        return templateRepository.findByIsActiveTrue();
    }

    @Transactional
    public ProjectDTO createProjectFromTemplate(UUID templateId, UUID teamId, String projectName) {
        ProjectTemplate template = templateRepository.findById(templateId)
            .orElseThrow(() -> new IllegalArgumentException("Template not found"));

        Project project = new Project();
        project.setName(projectName);
        project.setDescription(template.getDescription());
        project.setTeamId(teamId);
        
        // Set deadline based on estimated duration
        LocalDate startDate = LocalDate.now();
        project.setStartDate(startDate);
        project.setDeadline(startDate.plusDays(template.getEstimatedDurationDays()));
        
        // Apply template components
        template.getRequiredComponents().forEach(componentName -> {
            // Create components based on template
            createComponentFromTemplate(project, componentName);
        });
        
        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    private void createComponentFromTemplate(Project project, String componentName) {
        Component component = new Component();
        component.setName(componentName);
        component.setProject(project);
        project.getComponents().add(component);
    }
}