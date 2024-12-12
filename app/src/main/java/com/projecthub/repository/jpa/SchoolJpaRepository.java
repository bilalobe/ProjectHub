package com.projecthub.repository.jpa;

import com.projecthub.model.School;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * JPA repository interface for {@link School} entities.
 */
@Repository("jpaSchoolRepository")
@Profile("jpa")
public interface SchoolJpaRepository extends CrudRepository<School, UUID> {
    // Add relevant methods with UUID parameters and docstrings if needed
}