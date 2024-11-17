package com.projecthub.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecthub.model.Cohort;

@Repository
public interface CohortRepository extends JpaRepository<Cohort, Long> {
    List<Cohort> findBySchoolId(Long schoolId);
}