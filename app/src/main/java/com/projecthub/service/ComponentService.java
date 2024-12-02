package com.projecthub.service;

import com.projecthub.dto.ComponentSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.ComponentMapper;
import com.projecthub.model.Component;
import com.projecthub.repository.ComponentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComponentService {

    private static final Logger logger = LoggerFactory.getLogger(ComponentService.class);

    private final ComponentRepository componentRepository;
    private final ComponentMapper componentMapper;

    public ComponentService(ComponentRepository componentRepository, ComponentMapper componentMapper) {
        this.componentRepository = componentRepository;
        this.componentMapper = componentMapper;
    }

    public List<ComponentSummary> getAllComponents() {
        logger.info("Retrieving all components");
        return componentRepository.findAll().stream()
                .map(componentMapper::toComponentSummary)
                .collect(Collectors.toList());
    }

    public ComponentSummary getComponentById(Long id) {
        logger.info("Retrieving component with ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Component ID cannot be null");
        }
        Component component = componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component not found with ID: " + id));
        return componentMapper.toComponentSummary(component);
    }

    @Transactional
    public ComponentSummary saveComponent(ComponentSummary componentSummary) {
        logger.info("Saving component");
        if (componentSummary == null) {
            throw new IllegalArgumentException("ComponentSummary cannot be null");
        }
        Component component = componentMapper.toComponent(componentSummary);
        Component savedComponent = componentRepository.save(component);
        logger.info("Component saved with ID {}", savedComponent.getId());
        return componentMapper.toComponentSummary(savedComponent);
    }

    @Transactional
    public ComponentSummary updateComponent(Long id, ComponentSummary componentSummary) {
        logger.info("Updating component with ID {}", id);
        if (id == null || componentSummary == null) {
            throw new IllegalArgumentException("Component ID and ComponentSummary cannot be null");
        }
        Component existingComponent = componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component not found with ID: " + id));
        componentMapper.updateComponentFromSummary(componentSummary, existingComponent);
        Component updatedComponent = componentRepository.save(existingComponent);
        logger.info("Component updated with ID {}", updatedComponent.getId());
        return componentMapper.toComponentSummary(updatedComponent);
    }

    @Transactional
    public void deleteComponent(Long id) {
        logger.info("Deleting component with ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Component ID cannot be null");
        }
        Component component = componentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Component not found with ID: " + id));
        componentRepository.delete(component);
        logger.info("Component deleted");
    }

    public List<ComponentSummary> getComponentsByProjectId(Long projectId) {
        logger.info("Retrieving components for project ID {}", projectId);
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }
        return componentRepository.findByProjectId(projectId).stream()
                .map(componentMapper::toComponentSummary)
                .collect(Collectors.toList());
    }
}