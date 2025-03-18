package com.projecthub.base.component.api.controller;

import com.projecthub.base.component.api.dto.ComponentDTO;
import com.projecthub.base.component.api.rest.ComponentApi;
import com.projecthub.base.component.application.service.ComponentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/components")
public class ComponentController implements ComponentApi {

    private static final Logger logger = LoggerFactory.getLogger(ComponentController.class);
    private final ComponentService componentService;

    public ComponentController(final ComponentService componentService) {
        this.componentService = componentService;
    }

    @Override
    public ResponseEntity<List<ComponentDTO>> getAllComponents() {
        ComponentController.logger.info("Retrieving all components");
        return ResponseEntity.ok(this.componentService.getAllComponents());
    }

    @Override
    public ResponseEntity<ComponentDTO> getById(final UUID id) {
        ComponentController.logger.info("Retrieving component with ID {}", id);
        return ResponseEntity.ok(this.componentService.getComponentById(id));
    }

    @Override
    public ResponseEntity<ComponentDTO> saveComponent(@Valid @RequestBody final ComponentDTO component) {
        ComponentController.logger.info("Creating new component");
        return ResponseEntity.ok(this.componentService.saveComponent(component));
    }

    @Override
    public ResponseEntity<ComponentDTO> updateComponent(final UUID id, @Valid @RequestBody final ComponentDTO component) {
        ComponentController.logger.info("Updating component with ID {}", id);
        return ResponseEntity.ok(this.componentService.updateComponent(id, component));
    }

    @Override
    public ResponseEntity<Void> deleteById(final UUID id) {
        ComponentController.logger.info("Deleting component with ID {}", id);
        this.componentService.deleteComponent(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ComponentDTO>> getComponentsByProject(final UUID projectId) {
        ComponentController.logger.info("Retrieving components for project with ID {}", projectId);
        return ResponseEntity.ok(this.componentService.getComponentsByProjectId(projectId));
    }
}
