package com.projecthub.core.controllers;

import com.projecthub.core.api.ComponentApi;
import com.projecthub.core.dto.ComponentDTO;
import com.projecthub.core.services.project.ComponentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/components")
public class ComponentController implements ComponentApi {

    private static final Logger logger = LoggerFactory.getLogger(ComponentController.class);
    private final ComponentService componentService;

    public ComponentController(ComponentService componentService) {
        this.componentService = componentService;
    }

    @Override
    public ResponseEntity<List<ComponentDTO>> getAllComponents() {
        logger.info("Retrieving all components");
        return ResponseEntity.ok(componentService.getAllComponents());
    }

    @Override
    public ResponseEntity<ComponentDTO> getById(UUID id) {
        logger.info("Retrieving component with ID {}", id);
        return ResponseEntity.ok(componentService.getComponentById(id));
    }

    @Override
    public ResponseEntity<ComponentDTO> saveComponent(@Valid @RequestBody ComponentDTO component) {
        logger.info("Creating new component");
        return ResponseEntity.ok(componentService.saveComponent(component));
    }

    @Override
    public ResponseEntity<ComponentDTO> updateComponent(UUID id, @Valid @RequestBody ComponentDTO component) {
        logger.info("Updating component with ID {}", id);
        return ResponseEntity.ok(componentService.updateComponent(id, component));
    }

    @Override
    public ResponseEntity<Void> deleteById(UUID id) {
        logger.info("Deleting component with ID {}", id);
        componentService.deleteComponent(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ComponentDTO>> getComponentsByProject(UUID projectId) {
        logger.info("Retrieving components for project with ID {}", projectId);
        return ResponseEntity.ok(componentService.getComponentsByProjectId(projectId));
    }
}