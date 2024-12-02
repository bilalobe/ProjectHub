package com.projecthub.service;

import com.projecthub.dto.CohortSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.CohortMapper;
import com.projecthub.model.Cohort;
import com.projecthub.repository.CohortRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CohortService {

    private static final Logger logger = LoggerFactory.getLogger(CohortService.class);

    private final CohortRepository cohortRepository;
    private final CohortMapper cohortMapper;

    public CohortService(CohortRepository cohortRepository, CohortMapper cohortMapper) {
        this.cohortRepository = cohortRepository;
        this.cohortMapper = cohortMapper;
    }

    /**
     * Retrieves all cohorts.
     *
     * @return a list of CohortSummary objects
     */
    public List<CohortSummary> getAllCohorts() {
        logger.info("Retrieving all cohorts");
        return cohortRepository.findAll().stream()
                .map(cohortMapper::toCohortSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves cohorts by school ID.
     *
     * @param schoolId the ID of the school
     * @return a list of CohortSummary objects
     * @throws IllegalArgumentException if schoolId is null
     */
    public List<CohortSummary> getCohortsBySchoolId(Long schoolId) {
        logger.info("Retrieving cohorts for school ID {}", schoolId);
        if (schoolId == null) {
            throw new IllegalArgumentException("School ID cannot be null");
        }
        return cohortRepository.findBySchoolId(schoolId).stream()
                .map(cohortMapper::toCohortSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a cohort by its ID.
     *
     * @param id the ID of the cohort
     * @return the cohort summary
     * @throws IllegalArgumentException if the cohort ID is null
     * @throws ResourceNotFoundException if the cohort is not found
     */
    public CohortSummary getCohortById(Long id) {
        logger.info("Retrieving cohort with ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Cohort ID cannot be null");
        }
        Cohort cohort = cohortRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + id));
        return cohortMapper.toCohortSummary(cohort);
    }

    /**
     * Saves a new cohort.
     *
     * @param cohortSummary the cohort summary to save
     * @return the saved CohortSummary
     * @throws IllegalArgumentException if cohortSummary is null
     */
    @Transactional
    public CohortSummary saveCohort(CohortSummary cohortSummary) {
        logger.info("Saving cohort");
        if (cohortSummary == null) {
            throw new IllegalArgumentException("CohortSummary cannot be null");
        }
        Cohort cohort = cohortMapper.toCohort(cohortSummary);
        Cohort savedCohort = cohortRepository.save(cohort);
        logger.info("Cohort saved with ID {}", savedCohort.getId());
        return cohortMapper.toCohortSummary(savedCohort);
    }

    /**
     * Updates an existing cohort.
     *
     * @param id the ID of the cohort to update
     * @param cohortSummary the cohort summary with updated data
     * @return the updated CohortSummary
     * @throws IllegalArgumentException if id or cohortSummary is null
     * @throws ResourceNotFoundException if the cohort is not found
     */
    @Transactional
    public CohortSummary updateCohort(Long id, CohortSummary cohortSummary) {
        logger.info("Updating cohort with ID {}", id);
        if (id == null || cohortSummary == null) {
            throw new IllegalArgumentException("Cohort ID and CohortSummary cannot be null");
        }
        Cohort existingCohort = cohortRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + id));
        cohortMapper.updateCohortFromSummary(cohortSummary, existingCohort);
        Cohort updatedCohort = cohortRepository.save(existingCohort);
        logger.info("Cohort updated with ID {}", updatedCohort.getId());
        return cohortMapper.toCohortSummary(updatedCohort);
    }

    /**
     * Deletes a cohort by its ID.
     *
     * @param id the ID of the cohort to delete
     * @throws IllegalArgumentException if id is null
     * @throws ResourceNotFoundException if the cohort is not found
     */
    @Transactional
    public void deleteCohort(Long id) {
        logger.info("Deleting cohort with ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Cohort ID cannot be null");
        }
        Cohort cohort = cohortRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + id));
        cohortRepository.delete(cohort);
        logger.info("Cohort deleted");
    }
}