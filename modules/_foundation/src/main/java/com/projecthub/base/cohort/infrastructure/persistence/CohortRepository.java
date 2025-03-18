package com.projecthub.base.cohort.infrastructure.persistence;

import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.cohort.infrastructure.repository.CohortJpaRepository;
import com.projecthub.base.shared.repository.common.BaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CohortRepository implements BaseRepository<Cohort, UUID> {
    private final CohortJpaRepository jpaRepository;

    public Page<Cohort> findBySchoolId(final UUID schoolId, final Pageable pageable) {
        return this.jpaRepository.findBySchoolId(schoolId, pageable);
    }

    public Page<Cohort> findByIsActiveTrue(final Pageable pageable) {
        return this.jpaRepository.findByIsActiveTrue(pageable);
    }

    public Page<Cohort> findByIsArchivedTrue(final Pageable pageable) {
        return this.jpaRepository.findByIsArchivedTrue(pageable);
    }

    // Implementing BaseRepository methods
    @Override
    public List<Cohort> findAll() {
        return this.jpaRepository.findAll();
    }

    @Override
    public Optional<Cohort> findById(UUID id) {
        return this.jpaRepository.findById(id);
    }

    @Override
    public Cohort save(Cohort entity) {
        return this.jpaRepository.save(entity);
    }

    @Override
    public void delete(Cohort entity) {
        this.jpaRepository.delete(entity);
    }

    @Override
    public boolean existsById(UUID id) {
        return this.jpaRepository.existsById(id);
    }
}
