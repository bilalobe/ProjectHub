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

    public ComponentService(final ComponentJpaRepository componentRepository, final ComponentMapper componentMapper) {
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
    public ComponentDTO saveComponent(final ComponentDTO componentDTO) {
        ComponentService.logger.info("Creating a new component");
        if (null == componentDTO) {
            throw new IllegalArgumentException("ComponentDTO cannot be null");
        }
        final Component component = this.componentMapper.toEntity(componentDTO);
        final Component savedComponent = this.componentRepository.save(component);
        ComponentService.logger.info("Component created with ID {}", savedComponent.getId());
        return this.componentMapper.toDto(savedComponent);
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
    public ComponentDTO updateComponent(final UUID id, final ComponentDTO componentDTO) {
        ComponentService.logger.info("Updating component with ID {}", id);
        if (null == componentDTO) {
            throw new IllegalArgumentException("ComponentDTO cannot be null");
        }
        final Component existingComponent = this.componentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ComponentService.COMPONENT_NOT_FOUND_MESSAGE + id));
        this.componentMapper.updateEntityFromDto(componentDTO, existingComponent);
        final Component updatedComponent = this.componentRepository.save(existingComponent);
        ComponentService.logger.info("Component updated with ID {}", updatedComponent.getId());
        return this.componentMapper.toDto(updatedComponent);
    }

    /**
     * Deletes a component by ID.
     *
     * @param id the ID of the component to delete
     * @throws ResourceNotFoundException if the component is not found
     */
    @Transactional
    public void deleteComponent(final UUID id) {
        ComponentService.logger.info("Deleting component with ID {}", id);
        if (!this.componentRepository.existsById(id)) {
            throw new ResourceNotFoundException(ComponentService.COMPONENT_NOT_FOUND_MESSAGE + id);
        }
        this.componentRepository.deleteById(id);
        ComponentService.logger.info("Component deleted with ID {}", id);
    }

    /**
     * Retrieves a component by ID.
     *
     * @param id the ID of the component to retrieve
     * @return the component DTO
     * @throws ResourceNotFoundException if the component is not found
     */
    public ComponentDTO getComponentById(final UUID id) {
        ComponentService.logger.info("Retrieving component with ID {}", id);
        final Component component = this.componentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ComponentService.COMPONENT_NOT_FOUND_MESSAGE + id));
        return this.componentMapper.toDto(component);
    }

    /**
     * Retrieves all components.
     *
     * @return a list of component DTOs
     */
    public List<ComponentDTO> getAllComponents() {
        ComponentService.logger.info("Retrieving all components");
        return this.componentRepository.findAll().stream()
            .map(this.componentMapper::toDto)
            .toList();
    }

    /**
     * Retrieves components by project ID.
     *
     * @param projectId the ID of the project
     * @return a list of component DTOs
     */
    public List<ComponentDTO> getComponentsByProjectId(final UUID projectId) {
        ComponentService.logger.info("Retrieving components for project ID {}", projectId);
        return this.componentRepository.findByProjectId(projectId).stream()
            .map(this.componentMapper::toDto)
            .toList();
    }
}
