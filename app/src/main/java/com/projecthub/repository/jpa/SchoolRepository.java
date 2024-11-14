package com.projecthub.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecthub.model.School;
import com.projecthub.repository.custom.CustomSchoolRepository;

@Repository("postgresSchoolRepository")
public interface SchoolRepository extends JpaRepository<School, Long>, CustomSchoolRepository {
    // Custom query methods can be added here
}