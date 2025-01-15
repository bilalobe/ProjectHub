package com.projecthub.base.school.infrastructure.persistence;

import com.projecthub.base.school.domain.entity.School;
import com.projecthub.base.school.domain.repository.SchoolRepository;
import com.projecthub.base.school.infrastructure.persistence.jpa.SchoolJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SchoolPersistenceAdapter implements SchoolRepository {
    private final SchoolJpaRepository jpaRepository;

    @Override
    public School save(final School school) {
        return this.jpaRepository.save(school);
    }

    @Override
    public Optional<School> findById(final UUID id) {
        return this.jpaRepository.findById(id);
    }

    @Override
    public Optional<School> findByIdAndArchivedFalse(final UUID id) {
        return this.jpaRepository.findByIdAndArchivedFalse(id);
    }

    @Override
    public Page<School> findAll(final Pageable pageable) {
        return this.jpaRepository.findAll(pageable);
    }

    @Override
    public Page<School> findAll(final Specification<School> spec, final Pageable pageable) {
        return this.jpaRepository.findAll(spec, pageable);
    }

    @Override
    public Page<School> findByArchivedFalse(final Pageable pageable) {
        return this.jpaRepository.findByArchivedFalse(pageable);
    }

    @Override
    public Page<School> findByArchivedTrue(final Pageable pageable) {
        return this.jpaRepository.findByArchivedTrue(pageable);
    }

    @Override
    public Page<School> searchActiveSchools(final String search, final Pageable pageable) {
        return this.jpaRepository.searchActiveSchools(search, pageable);
    }

    @Override
    public void delete(final School school) {
        this.jpaRepository.delete(school);
    }

    @Override
    public boolean existsByCode(final String code) {
        return this.jpaRepository.existsByCode(code);
    }

    @Override
    public long count() {
        return this.jpaRepository.count();
    }

    @Override
    public void deleteById(final UUID id) {
        this.jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        this.jpaRepository.deleteAll();
    }

    @Override
    public void deleteAll(final Iterable<? extends School> schools) {
        this.jpaRepository.deleteAll(schools);
    }

    @Override
    public void deleteAllById(final Iterable<? extends UUID> ids) {
        this.jpaRepository.deleteAllById(ids);
    }

    @Override
    public boolean existsById(final UUID id) {
        return this.jpaRepository.existsById(id);
    }

    @Override
    public Iterable<School> findAll() {
        return this.jpaRepository.findAll();
    }

    @Override
    public Iterable<School> findAllById(final Iterable<UUID> ids) {
        return this.jpaRepository.findAllById(ids);
    }
}
