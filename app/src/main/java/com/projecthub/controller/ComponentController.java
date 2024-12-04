package com.projecthub.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecthub.service.ComponentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;

import com.projecthub.dto.ComponentDTO;
import com.projecthub.exception.ResourceNotFoundException;

@Aspect
@Component

@RequestMapping("/api/components")
@Tag(name = "Component API", description = "Operations pertaining to components in ProjectHub")
@RestController
public class ComponentController {

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
        try {
            componentService.deleteComponent(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }
}