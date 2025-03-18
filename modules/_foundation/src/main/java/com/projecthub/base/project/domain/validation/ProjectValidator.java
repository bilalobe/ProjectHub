package com.projecthub.base.project.domain.validation;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.infrastructure.repository.ProjectRepository;
import com.projecthub.base.shared.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    
    private final ProjectRepository projectRepository;

    public ValidationResult validateCreate(ProjectDTO project) {
        List<String> errors = new ArrayList<>();

        validateName(project.name(), project.teamId(), errors);
        validateDates(project.startDate(), project.deadline(), errors);
        
        return new ValidationResult(errors);
    }

    public ValidationResult validateUpdate(UUID projectId, ProjectDTO project) {
        List<String> errors = new ArrayList<>();

        // Allow same name for same project ID
        validateNameForUpdate(projectId, project.name(), project.teamId(), errors);
        validateDates(project.startDate(), project.deadline(), errors);
        
        return new ValidationResult(errors);
    }

    private void validateName(String name, UUID teamId, List<String> errors) {
        if (projectRepository.existsByNameAndTeamId(name, teamId)) {
            errors.add("Project with this name already exists for the team");
        }
    }

    private void validateNameForUpdate(UUID projectId, String name, UUID teamId, List<String> errors) {
        if (projectRepository.existsByNameAndTeamId(name, teamId)) {
            projectRepository.findById(projectId).ifPresent(existingProject -> {
                if (!existingProject.getName().equals(name)) {
                    errors.add("Project with this name already exists for the team");
                }
            });
        }
    }

    private void validateDates(LocalDate startDate, LocalDate deadline, List<String> errors) {
        if (startDate != null && deadline != null && deadline.isBefore(startDate)) {
            errors.add("Deadline cannot be before start date");
        }
        
        if (deadline != null && deadline.isBefore(LocalDate.now())) {
            errors.add("Deadline cannot be in the past");
        }
    }
}