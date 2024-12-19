package com.projecthub.core.services.project;

import com.projecthub.core.dto.ProjectDTO;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.mappers.ProjectMapper;
import com.projecthub.core.models.Project;
import com.projecthub.core.repositories.jpa.ProjectJpaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing projects.
 */
@Service
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private static final String PROJECT_NOT_FOUND_ERROR = "Project not found with ID: ";

    private final ProjectJpaRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectService self;

    public ProjectService(ProjectJpaRepository projectRepository, ProjectMapper projectMapper, ProjectService self) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.self = self;
    }

    /**
     * Creates a new project.
     *
     * @param projectDTO the project data transfer object
     * @return the created project DTO
     * @throws IllegalArgumentException if projectDTO is null
     */
    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        logger.info("Creating a new project");
        if (projectDTO == null) {
            throw new IllegalArgumentException("ProjectDTO cannot be null");
        }
        Project project = projectMapper.toProject(projectDTO);
        Project savedProject = projectRepository.save(project);
        logger.info("Project created with ID {}", savedProject.getId());
        return projectMapper.toProjectDTO(savedProject);
    }

    /**
     * Updates an existing project.
     *
     * @param id         the ID of the project to update
     * @param projectDTO the project data transfer object
     * @return the updated project DTO
     * @throws ResourceNotFoundException if the project is not found
     */
    @Transactional
    public ProjectDTO updateProject(UUID id, ProjectDTO projectDTO) {
        logger.info("Updating project with ID {}", id);
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND_ERROR + id));
        projectMapper.updateProjectFromDTO(projectDTO, existingProject);
        Project updatedProject = projectRepository.save(existingProject);
        logger.info("Project updated with ID {}", updatedProject.getId());
        return projectMapper.toProjectDTO(updatedProject);
    }

    /**
     * Deletes a project by ID.
     *
     * @param id the ID of the project to delete
     * @throws ResourceNotFoundException if the project is not found
     */
    @Transactional
    public void deleteProject(UUID id) {
        logger.info("Deleting project with ID {}", id);
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException(PROJECT_NOT_FOUND_ERROR + id);
        }
        projectRepository.deleteById(id);
        logger.info("Project deleted with ID {}", id);
    }

    /**
     * Retrieves a project by ID.
     *
     * @param id the ID of the project to retrieve
     * @return the project DTO
     * @throws ResourceNotFoundException if the project is not found
     */
    public ProjectDTO getProjectById(UUID id) {
        logger.info("Retrieving project with ID {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROJECT_NOT_FOUND_ERROR + id));
        return projectMapper.toProjectDTO(project);
    }

    /**
     * Retrieves all projects.
     *
     * @return a list of project DTOs
     */
    public List<ProjectDTO> getAllProjects() {
        logger.info("Retrieving all projects");
        return projectRepository.findAll().stream()
                .map(projectMapper::toProjectDTO)
                .toList();
    }

    /**
     * Saves a project (creates or updates).
     *
     * @param projectDTO the project data transfer object
     * @return the saved project DTO
     * @throws IllegalArgumentException if projectDTO is null
     */
    @Transactional
    public ProjectDTO saveProject(ProjectDTO projectDTO) {
        if (projectDTO == null) {
            throw new IllegalArgumentException("ProjectDTO cannot be null");
        }
        if (projectDTO.getId() != null) {
            return self.updateProject(projectDTO.getId(), projectDTO);
        } else {
            return self.createProject(projectDTO);
        }
    }
}