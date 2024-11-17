package com.projecthub.repository.custom;

import com.projecthub.model.Cohort;
import java.util.List;
import java.util.Optional;

public interface CustomCohortRepository {

    Cohort save(Cohort cohort);

    List<Cohort> findAll();

    Optional<Cohort> findById(Long id);

    void deleteById(Long id);

    List<Cohort> findBySchoolId(Long schoolId);
}