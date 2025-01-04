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
    public School save(School school) {
        return jpaRepository.save(school);
    }

    @Override
    public Optional<School> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<School> findByIdAndArchivedFalse(UUID id) {
        return jpaRepository.findByIdAndArchivedFalse(id);
    }

    @Override
    public Page<School> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<School> findAll(Specification<School> spec, Pageable pageable) {
        return jpaRepository.findAll(spec, pageable);
    }

    @Override
    public Page<School> findByArchivedFalse(Pageable pageable) {
        return jpaRepository.findByArchivedFalse(pageable);
    }

    @Override
    public Page<School> findByArchivedTrue(Pageable pageable) {
        return jpaRepository.findByArchivedTrue(pageable);
    }

    @Override
    public Page<School> searchActiveSchools(String search, Pageable pageable) {
        return jpaRepository.searchActiveSchools(search, pageable);
    }

    @Override
    public void delete(School school) {
        jpaRepository.delete(school);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    @Override
    public void deleteAll(Iterable<? extends School> schools) {
        jpaRepository.deleteAll(schools);
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> ids) {
        jpaRepository.deleteAllById(ids);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Iterable<School> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Iterable<School> findAllById(Iterable<UUID> ids) {
        return jpaRepository.findAllById(ids);
    }
}
