package com.projecthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projecthub.dto.ComponentSummary;
import com.projecthub.service.ComponentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/components")
@Tag(name = "Component API", description = "Operations pertaining to components in ProjectHub")
@RestController
public class ComponentController {

    private final ComponentService componentService;

    @Autowired
    public ComponentController(ComponentService componentService) {
        this.componentService = componentService;
    }

    @GetMapping
    @Operation(summary = "View a list of all components", description = "Returns a list of all components.")
    public ResponseEntity<List<ComponentSummary>> getAllComponents() {
        List<ComponentSummary> components = componentService.getAllComponents();
        return ResponseEntity.ok(components);
    }

    @PostMapping
    @Operation(summary = "Save a new component", description = "Creates a new component with the provided details.")
    public ResponseEntity<ComponentSummary> saveComponent(@RequestBody ComponentSummary componentSummary) {
        ComponentSummary savedComponentSummary = componentService.saveComponent(componentSummary);
        return ResponseEntity.ok(savedComponentSummary);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a component by ID", description = "Deletes the component with the specified ID.")
    public ResponseEntity<Void> deleteComponent(@PathVariable Long id) {
        componentService.deleteComponent(id);
        return ResponseEntity.noContent().build();
    }
}