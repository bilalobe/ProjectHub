package com.projecthub.base.school.api.controller;

import com.projecthub.base.school.api.dto.SchoolDTO;
import com.projecthub.base.school.application.port.in.SchoolQuery;
import com.projecthub.base.school.domain.criteria.SchoolSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schools/queries")
public class SchoolQueryController {
    private final SchoolQuery schoolQuery;

    public SchoolQueryController(final SchoolQuery schoolQuery) {
        this.schoolQuery = schoolQuery;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolDTO> getById(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.schoolQuery.getSchoolById(id));
    }

    @GetMapping
    public ResponseEntity<Page<SchoolDTO>> getAll(final Pageable pageable) {
        return ResponseEntity.ok(this.schoolQuery.getAllSchools(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SchoolDTO>> searchSchools(final SchoolSearchCriteria criteria, final Pageable pageable) {
        return ResponseEntity.ok(this.schoolQuery.searchSchools(criteria, pageable));
    }

    @GetMapping("/active")
    public ResponseEntity<Page<SchoolDTO>> getActiveSchools(final Pageable pageable) {
        return ResponseEntity.ok(this.schoolQuery.getActiveSchools(pageable));
    }

    @GetMapping("/archived")
    public ResponseEntity<Page<SchoolDTO>> getArchivedSchools(final Pageable pageable) {
        return ResponseEntity.ok(this.schoolQuery.getArchivedSchools(pageable));
    }
}
