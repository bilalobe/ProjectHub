package com.projecthub.repository.csv;

import com.projecthub.model.School;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link School} entities.
 */
@Repository("csvSchoolRepository")
@Profile("csv")
public interface SchoolCsvRepository extends BaseCsvRepository<School, Long> {
}