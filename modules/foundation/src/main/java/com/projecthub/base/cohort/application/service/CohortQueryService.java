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

import org.springframework.data.domain.PageRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class CohortQueryService implements CohortQuery {
    private final CohortRepository cohortRepository;
    private final SchoolRepository schoolRepository;
    private final CohortMapper cohortMapper;

    @Override
    public CohortDTO getCohortById(final UUID id) {
        CohortQueryService.log.debug("Retrieving cohort with ID: {}", id);
        return this.cohortMapper.toDto(findCohortById(id));
    }

    @Override
    public Page<CohortDTO> getAllCohorts(final Pageable pageable) {
        CohortQueryService.log.debug("Retrieving all cohorts with pagination");
        return this.cohortRepository.findAll(pageable)
            .map(this.cohortMapper::toDto);
    }

    @Override
    public Page<CohortDTO> getCohortsBySchoolId(final UUID schoolId, final Pageable pageable) {
        CohortQueryService.log.debug("Retrieving cohorts for school ID: {} with pagination", schoolId);
        validateSchoolExists(schoolId);
        return this.cohortRepository.findBySchoolId(schoolId, pageable)
            .map(this.cohortMapper::toDto);
    }

    @Override
    public Page<CohortDTO> getActiveCohorts(final Pageable pageable) {
        CohortQueryService.log.debug("Retrieving active cohorts with pagination");
        return this.cohortRepository.findByIsActiveTrue(pageable)
            .map(this.cohortMapper::toDto);
    }

    @Override
    public Page<CohortDTO> getArchivedCohorts(final Pageable pageable) {
        CohortQueryService.log.debug("Retrieving archived cohorts with pagination");
        return this.cohortRepository.findByIsArchivedTrue(pageable)
            .map(this.cohortMapper::toDto);
    }

    @Override
    public Page<CohortDTO> searchCohorts(final String query, final Pageable pageable) {
        CohortQueryService.log.debug("Searching cohorts with query: {} and pagination", query);
        final Specification<Cohort> spec = (root, criteriaQuery, criteriaBuilder) -> {
            final String wildcardQuery = "%" + query.toLowerCase() + "%";
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), wildcardQuery),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("assignment").get("level")), wildcardQuery)
            );
        };
        return this.cohortRepository.findAll(spec, pageable)
            .map(this.cohortMapper::toDto);
    }

    public CohortDTO findById(final UUID id) {
        return this.cohortRepository.findById(id)
            .map(this.cohortMapper::toDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Cohort", id));
    }

    public CohortConnection findAll(final PageRequest pageRequest) {
        return this.cohortMapper.toConnection(
            this.cohortRepository.findAll(pageRequest)
                .map(this.cohortMapper::toDTO)
        );
    }

    public List<CohortDTO> findBySchoolId(final UUID schoolId) {
        return this.cohortRepository.findBySchoolId(schoolId)
            .stream()
            .map(this.cohortMapper::toDTO)
            .toList();
    }
}
