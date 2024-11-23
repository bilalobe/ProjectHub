package com.projecthub.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projecthub.dto.ComponentSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.repository.custom.CustomComponentRepository;
import com.projecthub.repository.jpa.ComponentRepository;

/**
 * Service class for managing components.
 */
@Service
public class ComponentService {

    private static final Logger logger = LoggerFactory.getLogger(ComponentService.class);

    private final CustomComponentRepository customComponentRepository;
    private final ComponentRepository jpaComponentRepository;

    @Autowired
    public ComponentService(
            @Qualifier("csvComponentRepository") CustomComponentRepository customComponentRepository,
            ComponentRepository jpaComponentRepository) {
        this.customComponentRepository = customComponentRepository;
        this.jpaComponentRepository = jpaComponentRepository;
    }

    /**
     * Retrieves all components.
     *
     * @return a list of ComponentSummary objects
     */
    public List<ComponentSummary> getAllComponents() {
        logger.info("Retrieving all components");
        return customComponentRepository.findAll().stream()
                .map(ComponentSummary::new)
                .collect(Collectors.toList());
    }

    /**
     * Saves a component.
     *
     * @param componentSummary the ComponentSummary to save
     * @return the saved ComponentSummary
     * @throws IllegalArgumentException if componentSummary is null
     */
    @Transactional
    public ComponentSummary saveComponent(ComponentSummary componentSummary) {
        logger.info("Saving component");
        if (componentSummary == null) {
            throw new IllegalArgumentException("ComponentSummary cannot be null");
        }
        ComponentSummary savedComponent = customComponentRepository.save(componentSummary);
        logger.info("Component saved with ID {}", savedComponent.getId());
        return savedComponent;
    }

    /**
     * Deletes a component by ID.
     *
     * @param id the ID of the Component to delete
     * @throws IllegalArgumentException if id is null
     * @throws ResourceNotFoundException if the component is not found
     */
    @Transactional
    public void deleteComponent(Long id) throws ResourceNotFoundException {
        logger.info("Deleting component with ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Component ID cannot be null");
        }
        if (((Optional<?>) customComponentRepository.findById(id)).isPresent()) {
            logger.info("Component found");
        } else {
            throw new ResourceNotFoundException("Component not found with ID: " + id);
        }
        customComponentRepository.deleteById(id);
        logger.info("Component deleted");
    }

    /**
     * Retrieves the list of components associated with a specific project ID.
     *
     * @param projectId the ID of the project
     * @return a list of ComponentSummary objects
     * @throws IllegalArgumentException if projectId is null
     */
    public List<ComponentSummary> getComponentsByProjectId(Long projectId) {
        logger.info("Retrieving components for project ID {}", projectId);
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }
        return jpaComponentRepository.findByProjectId(projectId).stream()
                .map(ComponentSummary::new)
                .collect(Collectors.toList());
    }
}