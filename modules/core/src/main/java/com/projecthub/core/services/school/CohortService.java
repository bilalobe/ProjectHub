package com.projecthub.core.services.school;

import com.projecthub.core.dto.CohortDTO;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.mappers.CohortMapper;
import com.projecthub.core.models.Cohort;
import com.projecthub.core.models.School;
import com.projecthub.core.repositories.jpa.CohortJpaRepository;
import com.projecthub.core.repositories.jpa.SchoolJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing cohorts.
 */
@Service
public class CohortService {

    private static final Logger logger = LoggerFactory.getLogger(CohortService.class);

    private final CohortJpaRepository cohortRepository;
    private final SchoolJpaRepository schoolRepository;
    private final CohortMapper cohortMapper;

    public CohortService(CohortJpaRepository cohortRepository, SchoolJpaRepository schoolRepository, CohortMapper cohortMapper) {
        this.cohortRepository = cohortRepository;
        this.schoolRepository = schoolRepository;
        this.cohortMapper = cohortMapper;
    }

    /**
     * Creates a new cohort.
     *
     * @param cohortDTO the cohort data transfer object
     * @return the saved cohort DTO
     * @throws IllegalArgumentException if cohortDTO is null
     */
    @Transactional
    public CohortDTO saveCohort(CohortDTO cohortDTO) {
        logger.info("Saving cohort");
        if (cohortDTO == null) {
            throw new IllegalArgumentException("CohortDTO cannot be null");
        }
        // Additional validation logic can be added here
        Cohort cohort = cohortMapper.toEntity(cohortDTO);
        Cohort savedCohort = cohortRepository.save(cohort);
        logger.info("Cohort saved with ID {}", savedCohort.getId());
        return cohortMapper.toDto(savedCohort);
    }

    /**
     * Retrieves all cohorts.
     *
     * @return a list of cohort DTOs
     */
    public List<CohortDTO> getAllCohorts() {
        logger.info("Retrieving all cohorts");
        return cohortRepository.findAll().stream()
                .map(cohortMapper::toDto)
                .toList();
    }

    /**
     * Retrieves cohorts by school ID.
     *
     * @param schoolId the ID of the school
     * @return a list of cohort DTOs
     */
    public List<CohortDTO> getCohortsBySchoolId(UUID schoolId) {
        logger.info("Retrieving cohorts for school ID {}", schoolId);
        return cohortRepository.findBySchoolId(schoolId).stream()
                .map(cohortMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a cohort by ID.
     *
     * @param id the ID of the cohort to retrieve
     * @return the cohort DTO
     * @throws ResourceNotFoundException if the cohort is not found
     */
    public CohortDTO getCohortById(UUID id) {
        logger.info("Retrieving cohort with ID {}", id);
        Cohort cohort = cohortRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + id));
        return cohortMapper.toDto(cohort);
    }

    /**
     * Updates an existing cohort.
     *
     * @param id        the ID of the cohort to update
     * @param cohortDTO the cohort data transfer object
     * @return the updated cohort DTO
     * @throws ResourceNotFoundException if the cohort is not found
     */
    @Transactional
    public CohortDTO updateCohort(UUID id, CohortDTO cohortDTO) {
        logger.info("Updating cohort with ID {}", id);
        Cohort existingCohort = cohortRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + id));
        cohortMapper.updateEntityFromDto(cohortDTO, existingCohort);
        Cohort updatedCohort = cohortRepository.save(existingCohort);
        logger.info("Cohort updated with ID {}", updatedCohort.getId());
        return cohortMapper.toDto(updatedCohort);
    }

    /**
     * Deletes a cohort by ID.
     *
     * @param id the ID of the cohort to delete
     */
    @Transactional
    public void deleteCohort(UUID id) {
        logger.info("Deleting cohort with ID {}", id);
        cohortRepository.deleteById(id);
        logger.info("Cohort deleted with ID {}", id);
    }

    /**
     * Retrieves a school by ID.
     *
     * @param schoolId the ID of the school to retrieve
     * @return the school entity
     * @throws ResourceNotFoundException if the school is not found
     */
    public School getSchoolById(UUID schoolId) {
        logger.info("Retrieving school with ID {}", schoolId);
        return schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));
    }
}