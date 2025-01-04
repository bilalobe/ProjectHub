package com.projecthub.base.school.application.service;

import com.projecthub.base.school.api.dto.SchoolDTO;
import com.projecthub.base.school.api.mapper.SchoolMapper;
import com.projecthub.base.school.application.port.in.SchoolQuery;
import com.projecthub.base.school.domain.criteria.SchoolSearchCriteria;
import com.projecthub.base.school.domain.entity.School;
import com.projecthub.base.school.domain.exception.SchoolNotFoundException;
import com.projecthub.base.school.domain.repository.SchoolRepository;
import com.projecthub.base.school.infrastructure.specification.SchoolSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SchoolQueryService implements SchoolQuery {
    private static final String PAGEABLE_CANNOT_BE_NULL = "Pageable cannot be null";
    private final SchoolRepository repository;
    private final SchoolMapper mapper;

    @Override
    public SchoolDTO getSchoolById(UUID id) {
        Objects.requireNonNull(id, "School ID cannot be null");
        log.debug("Fetching school: {}", id);

        return repository.findById(id)
            .map(mapper::toDto)
            .orElseThrow(() -> new SchoolNotFoundException(id));
    }

    @Override
    public Page<SchoolDTO> searchSchools(SchoolSearchCriteria criteria, Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        log.debug("Searching schools with criteria: {}", criteria);

        return repository.findAll(
            SchoolSpecification.withCriteria(criteria),
            pageable).map(mapper::toDto);
    }

    @Override
    public Page<SchoolDTO> getAllSchools(PageRequest pageRequest) {
        Objects.requireNonNull(pageRequest, "Page request cannot be null");
        log.debug("Fetching all schools with pagination: {}", pageRequest);

        return repository.findAll(pageRequest)
            .map(mapper::toDto);
    }

    @Override
    public Page<SchoolDTO> getActiveSchools(Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        log.debug("Fetching active schools");

        return repository.findByArchivedFalse(pageable)
            .map(mapper::toDto);
    }

    @Override
    public Page<SchoolDTO> getArchivedSchools(Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_CANNOT_BE_NULL);
        log.debug("Fetching archived schools");

        return repository.findByArchivedTrue(pageable)
            .map(mapper::toDto);
    }

    public School findActiveSchoolById(UUID id) {
        Objects.requireNonNull(id, "School ID cannot be null");
        log.debug("Fetching active school: {}", id);

        return repository.findByIdAndArchivedFalse(id)
            .orElseThrow(() -> new SchoolNotFoundException(id));
    }
}
