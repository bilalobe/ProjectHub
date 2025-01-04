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

    public Optional<Cohort> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    public Page<Cohort> findBySchoolId(UUID schoolId, Pageable pageable) {
        return jpaRepository.findBySchoolId(schoolId, pageable);
    }

    public Page<Cohort> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    public Cohort save(Cohort cohort) {
        return jpaRepository.save(cohort);
    }

    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    public Page<Cohort> findByIsActiveTrue(Pageable pageable) {
        return jpaRepository.findByIsActiveTrue(pageable);
    }

    public Page<Cohort> findByIsArchivedTrue(Pageable pageable) {
        return jpaRepository.findByIsArchivedTrue(pageable);
    }
}
