package com.projecthub.core.services;

import com.projecthub.core.dto.SchoolDTO;
import com.projecthub.core.mappers.SchoolMapper;
import com.projecthub.core.models.School;
import com.projecthub.core.repositories.jpa.SchoolJpaRepository;
import com.projecthub.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing schools.
 */
@Service
public class SchoolService {

    private static final Logger logger = LoggerFactory.getLogger(SchoolService.class);

    private final SchoolJpaRepository schoolRepository;
    private final SchoolMapper schoolMapper;

    public SchoolService(SchoolJpaRepository schoolRepository, SchoolMapper schoolMapper) {
        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
    }

    /**
     * Creates a new school.
     *
     * @param schoolDTO the school data transfer object
     * @return the saved school DTO
     * @throws IllegalArgumentException if schoolDTO is null
     */
    @Transactional
    public SchoolDTO saveSchool(SchoolDTO schoolDTO) {
        logger.info("Creating a new school");
        if (schoolDTO == null) {
            throw new IllegalArgumentException("SchoolDTO cannot be null");
        }
        School school = schoolMapper.toSchool(schoolDTO);
        School savedSchool = schoolRepository.save(school);
        logger.info("School created with ID {}", savedSchool.getId());
        return schoolMapper.toSchoolDTO(savedSchool);
    }

    /**
     * Updates an existing school.
     *
     * @param id        the ID of the school to update
     * @param schoolDTO the school data transfer object
     * @return the updated school DTO
     * @throws ResourceNotFoundException if the school is not found
     */
    @Transactional
    public SchoolDTO updateSchool(UUID id, SchoolDTO schoolDTO) {
        logger.info("Updating school with ID {}", id);
        School existingSchool = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + id));
        schoolMapper.updateSchoolFromDTO(schoolDTO, existingSchool);
        School updatedSchool = schoolRepository.save(existingSchool);
        logger.info("School updated with ID {}", updatedSchool.getId());
        return schoolMapper.toSchoolDTO(updatedSchool);
    }

    /**
     * Deletes a school by ID.
     *
     * @param id the ID of the school to delete
     */
    @Transactional
    public void deleteSchool(UUID id) {
        logger.info("Deleting school with ID {}", id);
        schoolRepository.deleteById(id);
        logger.info("School deleted with ID {}", id);
    }

    /**
     * Retrieves a school by ID.
     *
     * @param id the ID of the school to retrieve
     * @return the school DTO
     * @throws ResourceNotFoundException if the school is not found
     */
    public SchoolDTO getSchoolById(UUID id) {
        logger.info("Retrieving school with ID {}", id);
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + id));
        return schoolMapper.toSchoolDTO(school);
    }

    /**
     * Retrieves all schools.
     *
     * @return a list of school DTOs
     */
    public List<SchoolDTO> getAllSchools() {
        logger.info("Retrieving all schools");
        List<School> schools = (List<School>) schoolRepository.findAll();
        return schools.stream()
                .map(schoolMapper::toSchoolDTO)
                .toList();
    }
}