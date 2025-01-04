package com.projecthub.base.component.application.service;


import com.projecthub.base.component.api.dto.ComponentDTO;
import com.projecthub.base.component.domain.entity.Component;
import com.projecthub.base.component.infrastructure.mapper.ComponentMapper;
import com.projecthub.base.component.infrastructure.persistence.ComponentJpaRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing project components.
 */
@Service
public class ComponentService {

    private static final Logger logger = LoggerFactory.getLogger(ComponentService.class);
    private static final String COMPONENT_NOT_FOUND_MESSAGE = "Component not found with ID: ";

    private final ComponentJpaRepository componentRepository;
    private final ComponentMapper componentMapper;

    public ComponentService(ComponentJpaRepository componentRepository, ComponentMapper componentMapper) {
        this.componentRepository = componentRepository;
        this.componentMapper = componentMapper;
    }

    /**
     * Creates a new component.
     *
     * @param componentDTO the component data transfer object
     * @return the saved component DTO
     * @throws IllegalArgumentException if componentDTO is null
     */
    @Transactional
    public ComponentDTO saveComponent(ComponentDTO componentDTO) {
        logger.info("Creating a new component");
        if (componentDTO == null) {
            throw new IllegalArgumentException("ComponentDTO cannot be null");
        }
        Component component = componentMapper.toEntity(componentDTO);
        Component savedComponent = componentRepository.save(component);
        logger.info("Component created with ID {}", savedComponent.getId());
        return componentMapper.toDto(savedComponent);
    }

    /**
     * Updates an existing component.
     *
     * @param id           the ID of the component to update
     * @param componentDTO the component data transfer object
     * @return the updated component DTO
     * @throws ResourceNotFoundException if the component is not found
     */
    @Transactional
    public ComponentDTO updateComponent(UUID id, ComponentDTO componentDTO) {
        logger.info("Updating component with ID {}", id);
        if (componentDTO == null) {
            throw new IllegalArgumentException("ComponentDTO cannot be null");
        }
        Component existingComponent = componentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(COMPONENT_NOT_FOUND_MESSAGE + id));
        componentMapper.updateEntityFromDto(componentDTO, existingComponent);
        Component updatedComponent = componentRepository.save(existingComponent);
        logger.info("Component updated with ID {}", updatedComponent.getId());
        return componentMapper.toDto(updatedComponent);
    }

    /**
     * Deletes a component by ID.
     *
     * @param id the ID of the component to delete
     * @throws ResourceNotFoundException if the component is not found
     */
    @Transactional
    public void deleteComponent(UUID id) {
        logger.info("Deleting component with ID {}", id);
        if (!componentRepository.existsById(id)) {
            throw new ResourceNotFoundException(COMPONENT_NOT_FOUND_MESSAGE + id);
        }
        componentRepository.deleteById(id);
        logger.info("Component deleted with ID {}", id);
    }

    /**
     * Retrieves a component by ID.
     *
     * @param id the ID of the component to retrieve
     * @return the component DTO
     * @throws ResourceNotFoundException if the component is not found
     */
    public ComponentDTO getComponentById(UUID id) {
        logger.info("Retrieving component with ID {}", id);
        Component component = componentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(COMPONENT_NOT_FOUND_MESSAGE + id));
        return componentMapper.toDto(component);
    }

    /**
     * Retrieves all components.
     *
     * @return a list of component DTOs
     */
    public List<ComponentDTO> getAllComponents() {
        logger.info("Retrieving all components");
        return componentRepository.findAll().stream()
            .map(componentMapper::toDto)
            .toList();
    }

    /**
     * Retrieves components by project ID.
     *
     * @param projectId the ID of the project
     * @return a list of component DTOs
     */
    public List<ComponentDTO> getComponentsByProjectId(UUID projectId) {
        logger.info("Retrieving components for project ID {}", projectId);
        return componentRepository.findByProjectId(projectId).stream()
            .map(componentMapper::toDto)
            .toList();
    }
}
