package com.projecthub.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projecthub.dto.SchoolSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.SchoolMapper;
import com.projecthub.model.School;
import com.projecthub.repository.custom.CustomSchoolRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Service
@Tag(name = "School Service", description = "Operations pertaining to schools in ProjectHub")
public class SchoolService {

    private static final Logger logger = LoggerFactory.getLogger(SchoolService.class);

    private final CustomSchoolRepository schoolRepository;
    private final SchoolMapper schoolMapper;

    public SchoolService(@Qualifier("csvSchoolRepository") CustomSchoolRepository schoolRepository, SchoolMapper schoolMapper) {
        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
    }

    /**
     * Retrieves a list of all schools.
     *
     * @return a list of SchoolSummary
     */
    @Operation(summary = "View a list of all schools", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved list of schools")
    })
    public List<SchoolSummary> getAllSchools() {
        logger.info("Retrieving all schools");
        return StreamSupport.stream(schoolRepository.findAll().spliterator(), false)
                .map(schoolMapper::toSchoolSummary)
                .collect(Collectors.toList());
    }

    /**
     * Saves a school.
     *
     * @param schoolSummary the school summary to save
     * @return the saved SchoolSummary
     * @throws IllegalArgumentException if schoolSummary is null
     */
    @Operation(summary = "Save a school", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully saved school"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid school data")
    })
    @Transactional
    public SchoolSummary saveSchool(SchoolSummary schoolSummary) {
        logger.info("Saving school");
        if (schoolSummary == null) {
            throw new IllegalArgumentException("SchoolSummary cannot be null");
        }
        School school = schoolMapper.toSchool(schoolSummary);
        School savedSchool = schoolRepository.save(school);
        logger.info("School saved with ID {}", savedSchool.getId());
        return schoolMapper.toSchoolSummary(savedSchool);
    }

    /**
     * Deletes a school by ID.
     *
     * @param id the ID of the school to delete
     * @throws IllegalArgumentException if id is null
     * @throws ResourceNotFoundException if the school is not found
     */
    @Operation(summary = "Delete a school by ID", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully deleted school"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "School not found")
    })
    @Transactional
    public void deleteSchool(Long id) throws ResourceNotFoundException {
        logger.info("Deleting school with ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("School ID cannot be null");
        }
        if (!schoolRepository.existsById(id)) {
            throw new ResourceNotFoundException("School not found with ID: " + id);
        }
        schoolRepository.deleteById(id);
        logger.info("School deleted");
    }

    /**
     * Retrieves a school by its ID.
     *
     * @param id the ID of the school
     * @return the SchoolSummary
     * @throws IllegalArgumentException if id is null
     * @throws ResourceNotFoundException if the school is not found
     */
    public SchoolSummary getSchoolById(Long id) throws ResourceNotFoundException {
        logger.info("Retrieving school with ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("School ID cannot be null");
        }
        return schoolRepository.findById(id)
                .map(schoolMapper::toSchoolSummary)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + id));
    }
}