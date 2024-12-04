package com.projecthub.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projecthub.dto.SchoolDTO;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.SchoolService;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "School API", description = "Operations pertaining to schools in ProjectHub")
@RestController
@RequestMapping("/api/schools")
public class SchoolController {

    private static final Logger logger = LoggerFactory.getLogger(SchoolController.class);

    private final SchoolService schoolService;

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @Operation(summary = "Get all schools")
    @GetMapping
    public ResponseEntity<List<SchoolDTO>> getAllSchools() {
        logger.info("Retrieving all schools");
        List<SchoolDTO> schools = schoolService.getAllSchools();
        return ResponseEntity.ok(schools);
    }

    @Operation(summary = "Get school by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SchoolDTO> getSchoolById(@PathVariable UUID id) {
        logger.info("Retrieving school with ID {}", id);
        try {
            SchoolDTO school = schoolService.getSchoolById(id);
            return ResponseEntity.ok(school);
        } catch (ResourceNotFoundException e) {
            logger.error("School not found with ID {}", id, e);
            return ResponseEntity.status(404).body(null);
        }
    }

    @Operation(summary = "Create a new school")
    @PostMapping
    public ResponseEntity<SchoolDTO> createSchool(@Valid @RequestBody SchoolDTO schoolSummary) {
        logger.info("Creating a new school");
        try {
            SchoolDTO createdSchool = schoolService.saveSchool(schoolSummary);
            return ResponseEntity.ok(createdSchool);
        } catch (Exception e) {
            logger.error("Error creating school", e);
            return ResponseEntity.status(400).body(null);
        }
    }

    @Operation(summary = "Update an existing school")
    @PutMapping("/{id}")
    public ResponseEntity<SchoolDTO> updateSchool(@PathVariable UUID id, @Valid @RequestBody SchoolDTO schoolSummary) {
        logger.info("Updating school with ID {}", id);
        try {
            SchoolDTO updatedSchool = schoolService.updateSchool(id, schoolSummary);
            return ResponseEntity.ok(updatedSchool);
        } catch (ResourceNotFoundException e) {
            logger.error("School not found with ID {}", id, e);
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            logger.error("Error updating school", e);
            return ResponseEntity.status(400).body(null);
        }
    }

    @Operation(summary = "Delete a school by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSchool(@PathVariable UUID id) {
        logger.info("Deleting school with ID {}", id);
        try {
            schoolService.deleteSchool(id);
            return ResponseEntity.ok("School deleted successfully");
        } catch (ResourceNotFoundException e) {
            logger.error("School not found with ID {}", id, e);
            return ResponseEntity.status(404).body("School not found with ID " + id);
        }
    }
}