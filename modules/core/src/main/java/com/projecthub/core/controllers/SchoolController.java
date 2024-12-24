package com.projecthub.core.controllers;

import com.projecthub.core.api.SchoolApi;
import com.projecthub.core.dto.SchoolDTO;
import com.projecthub.core.services.school.SchoolService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schools")
public class SchoolController implements SchoolApi {

    private static final Logger logger = LoggerFactory.getLogger(SchoolController.class);
    private final SchoolService schoolService;

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @Override
    public ResponseEntity<List<SchoolDTO>> getAllSchools() {
        logger.info("Retrieving all schools");
        return ResponseEntity.ok(schoolService.getAllSchools());
    }

    @Override
    public ResponseEntity<SchoolDTO> getById(UUID id) {
        logger.info("Retrieving school with ID {}", id);
        return ResponseEntity.ok(schoolService.getSchoolById(id));
    }

    @Override
    public ResponseEntity<SchoolDTO> createSchool(@Valid @RequestBody SchoolDTO school) {
        logger.info("Creating new school");
        return ResponseEntity.ok(schoolService.saveSchool(school));
    }

    @Override
    public ResponseEntity<SchoolDTO> updateSchool(UUID id, @Valid @RequestBody SchoolDTO school) {
        logger.info("Updating school with ID {}", id);
        return ResponseEntity.ok(schoolService.updateSchool(id, school));
    }

    @Override
    public ResponseEntity<Void> deleteById(UUID id) {
        logger.info("Deleting school with ID {}", id);
        schoolService.deleteSchool(id);
        return ResponseEntity.noContent().build();
    }
}