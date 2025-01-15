package com.projecthub.base.cohort.infrastructure.persistence;

import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.cohort.infrastructure.repository.CohortJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CohortRepository {
    private final CohortJpaRepository jpaRepository;

    public Optional<Cohort> findById(final UUID id) {
        return this.jpaRepository.findById(id);
    }

    public Page<Cohort> findBySchoolId(final UUID schoolId, final Pageable pageable) {
        return this.jpaRepository.findBySchoolId(schoolId, pageable);
    }

    public Page<Cohort> findAll(final Pageable pageable) {
        return this.jpaRepository.findAll(pageable);
    }

    public Cohort save(final Cohort cohort) {
        return this.jpaRepository.save(cohort);
    }

    public void deleteById(final UUID id) {
        this.jpaRepository.deleteById(id);
    }

    public boolean existsById(final UUID id) {
        return this.jpaRepository.existsById(id);
    }

    public Page<Cohort> findByIsActiveTrue(final Pageable pageable) {
        return this.jpaRepository.findByIsActiveTrue(pageable);
    }

    public Page<Cohort> findByIsArchivedTrue(final Pageable pageable) {
        return this.jpaRepository.findByIsArchivedTrue(pageable);
    }
}
