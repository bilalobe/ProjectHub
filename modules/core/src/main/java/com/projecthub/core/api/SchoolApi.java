package com.projecthub.core.api;

import com.projecthub.core.dto.SchoolDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Schools", description = "School management API")
@RequestMapping("/api/v1/schools")
public interface SchoolApi extends BaseApi<SchoolDTO, UUID> {

    @Operation(summary = "Get all schools")
    @GetMapping
    ResponseEntity<List<SchoolDTO>> getAllSchools();

    @Operation(summary = "Create a new school")
    @PostMapping
    ResponseEntity<SchoolDTO> createSchool(@Valid @RequestBody SchoolDTO school);

    @Operation(summary = "Update school")
    @PutMapping("/{id}")
    ResponseEntity<SchoolDTO> updateSchool(
            @PathVariable UUID id,
            @Valid @RequestBody SchoolDTO school);
}
