package com.projecthub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projecthub.dto.CohortSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.repository.jpa.CohortRepository;

/**
 * Service class for managing Cohorts.
 */
@Service
public class CohortService {

    private static final Logger logger = LoggerFactory.getLogger(CohortService.class);

    private final CohortRepository cohortRepository;

    /**
     * Constructs a new CohortService.
     *
     * @param cohortRepository the Cohort repository
     */
    @Autowired
    public CohortService(CohortRepository cohortRepository) {
        this.cohortRepository = cohortRepository;
    }

    /**
     * Retrieves all cohorts.
     *
     * @return a list of CohortSummary
     */
    public List<CohortSummary> getAllCohorts() {
        logger.info("Retrieving all cohorts");
        return cohortRepository.findAll().stream()
                .map(CohortSummary::new)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves cohorts by school ID.
     *
     * @param schoolId the school ID
     * @return a list of CohortSummary
     * @throws IllegalArgumentException if schoolId is null
     */
    public List<CohortSummary> getCohortsBySchoolId(Long schoolId) {
        logger.info("Retrieving cohorts for school ID {}", schoolId);
        if (schoolId == null) {
            throw new IllegalArgumentException("School ID cannot be null");
        }
        return cohortRepository.findBySchoolId(schoolId).stream()
                .map(CohortSummary::new)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a cohort by its ID.
     *
     * @param id the cohort ID
     * @return the CohortSummary
     * @throws IllegalArgumentException if id is null
     * @throws ResourceNotFoundException if the cohort is not found
     */
    public CohortSummary getCohortById(Long id) throws ResourceNotFoundException {
        logger.info("Retrieving cohort with ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Cohort ID cannot be null");
        }
        return cohortRepository.findById(id)
                .map(CohortSummary::new)
                .orElseThrow(() -> new ResourceNotFoundException("Cohort not found with ID: " + id));
    }

    /**
     * Saves a cohort.
     *
     * @param cohortSummary the CohortSummary to save
     * @return the saved CohortSummary
     * @throws IllegalArgumentException if cohortSummary is null
     */
    @Transactional
    public CohortSummary saveCohort(CohortSummary cohortSummary) {
        logger.info("Saving cohort");
        if (cohortSummary == null) {
            throw new IllegalArgumentException("CohortSummary cannot be null");
        }
        CohortSummary savedCohort = new CohortSummary(cohortRepository.save(cohortSummary.toCohort()));
        logger.info("Cohort saved with ID {}", savedCohort.getId());
        return savedCohort;
    }

    /**
     * Deletes a cohort by its ID.
     *
     * @param id the cohort ID to delete
     * @throws IllegalArgumentException if id is null
     * @throws ResourceNotFoundException if the cohort is not found
     */
    @Transactional
    public void deleteCohort(Long id) throws ResourceNotFoundException {
        logger.info("Deleting cohort with ID {}", id);
        if (id == null) {
            throw new IllegalArgumentException("Cohort ID cannot be null");
        }
        if (!cohortRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cohort not found with ID: " + id);
        }
        cohortRepository.deleteById(id);
        logger.info("Cohort deleted");
    }
}