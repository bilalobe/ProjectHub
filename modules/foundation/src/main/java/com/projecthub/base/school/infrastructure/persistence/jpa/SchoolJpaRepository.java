package com.projecthub.base.school.infrastructure.persistence.jpa;

import com.projecthub.base.school.domain.entity.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolJpaRepository extends JpaRepository<School, UUID>, JpaSpecificationExecutor<School> {
    Optional<School> findByIdAndArchivedFalse(UUID id);

    Page<School> findByArchivedFalse(Pageable pageable);

    Page<School> findByArchivedTrue(Pageable pageable);

    @Query("""
        SELECT s FROM School s
        WHERE s.archived = false
        AND (LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(s.address.city) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY s.name
        """)
    Page<School> searchActiveSchools(String search, Pageable pageable);
}
