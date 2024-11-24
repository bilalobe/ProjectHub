package com.projecthub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projecthub.service.SchoolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.projecthub.dto.SchoolSummary;
import com.projecthub.exception.ResourceNotFoundException;

@Tag(name = "School API", description = "Operations pertaining to schools in ProjectHub")
@RestController
@RequestMapping("/schools")
public class SchoolController {

    private final SchoolService schoolService;

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @GetMapping
    public List<SchoolSummary> getAllSchools() {
        return schoolService.getAllSchools();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolSummary> getSchoolById(@PathVariable Long id) {
        SchoolSummary school = schoolService.getSchoolById(id);
        if (school != null) {
            return ResponseEntity.ok(school);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    @Operation(summary = "Create a new school")
    @PostMapping
    public ResponseEntity<SchoolSummary> createSchool(@Valid @RequestBody SchoolSummary schoolSummary) {
        try {
            SchoolSummary createdSchool = schoolService.saveSchool(schoolSummary);
            return ResponseEntity.ok(createdSchool);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @Operation(summary = "Update an existing school")
    @PutMapping("/{id}")
    public ResponseEntity<SchoolSummary> updateSchool(@PathVariable Long id, @Valid @RequestBody SchoolSummary schoolSummary) {
        try {
            SchoolSummary updatedSchool = schoolService.saveSchool(new SchoolSummary(
                id,
                schoolSummary.getName(),
                schoolSummary.getAddress()
            ));
            return ResponseEntity.ok(updatedSchool);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @Operation(summary = "Delete a school by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSchool(@PathVariable Long id) {
        try {
            schoolService.deleteSchool(id);
            return ResponseEntity.ok("School deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("School not found");
        }
    }
}