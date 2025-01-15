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
    public SchoolDTO getSchoolById(final UUID id) {
        Objects.requireNonNull(id, "School ID cannot be null");
        SchoolQueryService.log.debug("Fetching school: {}", id);

        return this.repository.findById(id)
            .map(this.mapper::toDto)
            .orElseThrow(() -> new SchoolNotFoundException(id));
    }

    @Override
    public Page<SchoolDTO> searchSchools(final SchoolSearchCriteria criteria, final Pageable pageable) {
        Objects.requireNonNull(pageable, SchoolQueryService.PAGEABLE_CANNOT_BE_NULL);
        SchoolQueryService.log.debug("Searching schools with criteria: {}", criteria);

        return this.repository.findAll(
            SchoolSpecification.withCriteria(criteria),
            pageable).map(this.mapper::toDto);
    }

    @Override
    public Page<SchoolDTO> getAllSchools(final PageRequest pageRequest) {
        Objects.requireNonNull(pageRequest, "Page request cannot be null");
        SchoolQueryService.log.debug("Fetching all schools with pagination: {}", pageRequest);

        return this.repository.findAll(pageRequest)
            .map(this.mapper::toDto);
    }

    @Override
    public Page<SchoolDTO> getActiveSchools(final Pageable pageable) {
        Objects.requireNonNull(pageable, SchoolQueryService.PAGEABLE_CANNOT_BE_NULL);
        SchoolQueryService.log.debug("Fetching active schools");

        return this.repository.findByArchivedFalse(pageable)
            .map(this.mapper::toDto);
    }

    @Override
    public Page<SchoolDTO> getArchivedSchools(final Pageable pageable) {
        Objects.requireNonNull(pageable, SchoolQueryService.PAGEABLE_CANNOT_BE_NULL);
        SchoolQueryService.log.debug("Fetching archived schools");

        return this.repository.findByArchivedTrue(pageable)
            .map(this.mapper::toDto);
    }

    public School findActiveSchoolById(final UUID id) {
        Objects.requireNonNull(id, "School ID cannot be null");
        SchoolQueryService.log.debug("Fetching active school: {}", id);

        return this.repository.findByIdAndArchivedFalse(id)
            .orElseThrow(() -> new SchoolNotFoundException(id));
    }
}
