package com.projecthub.core.api;

import com.projecthub.core.dto.ComponentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Components", description = "Component management API")
@RequestMapping("/api/v1/components")
public interface ComponentApi extends BaseApi<ComponentDTO, UUID> {

    @Operation(summary = "Get all components", 
            responses = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved components")
            })
    @GetMapping
    ResponseEntity<List<ComponentDTO>> getAllComponents();

    @Operation(summary = "Create a new component",
            responses = {
                @ApiResponse(responseCode = "201", description = "Component created successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid component data")
            })
    @PostMapping 
    ResponseEntity<ComponentDTO> saveComponent(@Valid @RequestBody ComponentDTO component);

    @Operation(summary = "Get components by project",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved project components")
            })
    @GetMapping("/project/{projectId}")
    ResponseEntity<List<ComponentDTO>> getComponentsByProject(@PathVariable UUID projectId);

    @Operation(summary = "Update component",
            responses = {
                @ApiResponse(responseCode = "200", description = "Component updated successfully")
            })
    @PutMapping("/{id}")
    ResponseEntity<ComponentDTO> updateComponent(
            @PathVariable UUID id,
            @Valid @RequestBody ComponentDTO component);
}