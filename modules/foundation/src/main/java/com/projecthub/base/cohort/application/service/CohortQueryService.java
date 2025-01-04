package com.projecthub.base.cohort.application.service;

import com.projecthub.base.cohort.api.dto.CohortDTO;
import com.projecthub.base.cohort.api.mapper.CohortMapper;
import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.cohort.infrastructure.persistence.CohortRepository;
import com.projecthub.base.school.domain.repository.SchoolRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CohortQueryService implements CohortQuery {
    private final CohortRepository cohortRepository;
    private final SchoolRepository schoolRepository;
    private final CohortMapper cohortMapper;

    @Override
    public CohortDTO getCohortById(UUID id) {
        log.debug("Retrieving cohort with ID: {}", id);
        return cohortMapper.toDto(findCohortById(id));
    }

    @Override
    public Page<CohortDTO> getAllCohorts(Pageable pageable) {
        log.debug("Retrieving all cohorts with pagination");
        return cohortRepository.findAll(pageable)
            .map(cohortMapper::toDto);
    }

    @Override
    public Page<CohortDTO> getCohortsBySchoolId(UUID schoolId, Pageable pageable) {
        log.debug("Retrieving cohorts for school ID: {} with pagination", schoolId);
        validateSchoolExists(schoolId);
        return cohortRepository.findBySchoolId(schoolId, pageable)
            .map(cohortMapper::toDto);
    }

    @Override
    public Page<CohortDTO> getActiveCohorts(Pageable pageable) {
        log.debug("Retrieving active cohorts with pagination");
        return cohortRepository.findByIsActiveTrue(pageable)
            .map(cohortMapper::toDto);
    }

    @Override
    public Page<CohortDTO> getArchivedCohorts(Pageable pageable) {
        log.debug("Retrieving archived cohorts with pagination");
        return cohortRepository.findByIsArchivedTrue(pageable)
            .map(cohortMapper::toDto);
    }

    @Override
    public Page<CohortDTO> searchCohorts(String query, Pageable pageable) {
        log.debug("Searching cohorts with query: {} and pagination", query);
        Specification<Cohort> spec = (root, criteriaQuery, criteriaBuilder) -> {
            String wildcardQuery = "%" + query.toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), wildcardQuery),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("assignment").get("level")), wildcardQuery)
            );
        };
        return cohortRepository.findAll(spec, pageable)
            .map(cohortMapper::toDto);
    }

    public CohortDTO findById(UUID id) {
        return cohortRepository.findById(id)
            .map(cohortMapper::toDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Cohort", id));
    }

    public CohortConnection findAll(PageRequest pageRequest) {
        return cohortMapper.toConnection(
            cohortRepository.findAll(pageRequest)
                .map(cohortMapper::toDTO)
        );
    }

    public List<CohortDTO> findBySchoolId(UUID schoolId) {
        return cohortRepository.findBySchoolId(schoolId)
            .stream()
            .map(cohortMapper::toDTO)
            .toList();
    }
}
