package com.projecthub.repository.custom;

import java.util.List;
import java.util.Optional;

import com.projecthub.model.School;

public interface CustomSchoolRepository {
    List<School> findAll();
    School save(School school);
    void deleteById(Long schoolId);
    Optional<School> findById(Long id);
}