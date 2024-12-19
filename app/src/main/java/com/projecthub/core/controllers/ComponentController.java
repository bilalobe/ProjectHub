package com.projecthub.core.controllers;

import com.projecthub.core.dto.ComponentDTO;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.services.project.ComponentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/components")
@Tag(name = "Component API", description = "Operations pertaining to components in ProjectHub")
@RestController
public class ComponentController {

    private static final Logger logger = LoggerFactory.getLogger(ComponentController.class);

    private final ComponentService componentService;

    public ComponentController(ComponentService componentService) {
        this.componentService = componentService;
    }

    @GetMapping
    @Operation(summary = "View a list of all components", description = "Returns a list of all components.")
    public ResponseEntity<List<ComponentDTO>> getAllComponents() {
        List<ComponentDTO> components = componentService.getAllComponents();
        return ResponseEntity.ok(components);
    }

    @PostMapping
    @Operation(summary = "Save a new component", description = "Creates a new component with the provided details.")
    public ResponseEntity<ComponentDTO> saveComponent(@Valid @RequestBody ComponentDTO componentSummary) {
        try {
            ComponentDTO savedComponentSummary = componentService.saveComponent(componentSummary);
            return ResponseEntity.ok(savedComponentSummary);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a component by ID", description = "Deletes the component with the specified ID.")
    public ResponseEntity<Void> deleteComponent(@PathVariable UUID id) {
        logger.info("Deleting component with ID {}", id);
        try {
            componentService.deleteComponent(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Void> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Resource not found", ex);
        return ResponseEntity.status(404).build();
    }
}