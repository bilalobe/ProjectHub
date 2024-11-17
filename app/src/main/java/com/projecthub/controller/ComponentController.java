package com.projecthub.controller;

import com.projecthub.model.Component;
import com.projecthub.service.ComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/components")
@Tag(name = "Component API", description = "Operations pertaining to components in ProjectHub")
public class ComponentController {

    private final ComponentService componentService;

    @Autowired
    public ComponentController(ComponentService componentService) {
        this.componentService = componentService;
    }

    @GetMapping
    @Operation(summary = "View a list of all components", description = "Returns a list of all components.")
    public ResponseEntity<List<Component>> getAllComponents() {
        List<Component> components = componentService.getAllComponents();
        return ResponseEntity.ok(components);
    }

    @PostMapping
    @Operation(summary = "Save a new component", description = "Creates a new component with the provided details.")
    public ResponseEntity<Component> saveComponent(@RequestBody Component component) {
        Component savedComponent = componentService.saveComponent(component);
        return ResponseEntity.ok(savedComponent);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a component by ID", description = "Deletes the component with the specified ID.")
    public ResponseEntity<Void> deleteComponent(@PathVariable Long id) {
        componentService.deleteComponent(id);
        return ResponseEntity.noContent().build();
    }
}