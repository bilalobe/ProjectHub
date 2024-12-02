package com.projecthub.repository.jpa;

import com.projecthub.model.School;
import com.projecthub.repository.SchoolRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository interface for {@link School} entities.
 */
@Repository("jpaSchoolRepository")
@Profile("jpa")
public interface SchoolJpaRepository extends CrudRepository<School, Long>, SchoolRepository {
}