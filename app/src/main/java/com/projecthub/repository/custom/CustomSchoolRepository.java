package com.projecthub.repository.custom;

import com.projecthub.model.School;

import java.util.List;
import java.util.Optional;

import com.projecthub.dto.SchoolSummary;

public interface CustomSchoolRepository {

    School save(School school);

    List<School> findAll();

    Optional<School> findById(Long id);

    void deleteById(Long id);

    public void save(SchoolSummary schoolSummary);
}