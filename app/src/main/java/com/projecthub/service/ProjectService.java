package com.projecthub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projecthub.model.Project;
import com.projecthub.repository.custom.CustomProjectRepository;

@Service
public class ProjectService {

    private final CustomProjectRepository projectRepository;

    public ProjectService(@Qualifier("csvProjectRepository") CustomProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return Optional.ofNullable(projectRepository.findProjectWithComponentsById(id));
    }

    public List<Project> getProjectsByTeamId(Long teamId) {
        return projectRepository.findAllByTeamId(teamId);
    }

    public Project getProjectWithComponentsById(Long id) {
        return projectRepository.findProjectWithComponentsById(id);
    }

    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}