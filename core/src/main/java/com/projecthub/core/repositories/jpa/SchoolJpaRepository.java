package com.projecthub.core.repositories.jpa;

import com.projecthub.core.models.School;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * JPA repository interface for {@link School} entities.
 */
@Repository("jpaSchoolRepository")
@Profile("jpa")
public interface SchoolJpaRepository extends JpaRepository<School, UUID> {
    // Additional methods can be added here if needed
}