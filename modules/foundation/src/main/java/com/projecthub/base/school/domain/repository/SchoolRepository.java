package com.projecthub.base.school.domain.repository;

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
public interface SchoolRepository extends JpaRepository<School, UUID>, JpaSpecificationExecutor<School> {

    @Query("SELECT s FROM School s WHERE s.archived = false ORDER BY s.name")
    Page<School> findActiveSchools(Pageable pageable);

    @Query("SELECT s FROM School s WHERE s.archived = true ORDER BY s.name")
    Page<School> findArchivedSchools(Pageable pageable);

    @Query("SELECT s FROM School s WHERE s.id = :id AND s.archived = false")
    Optional<School> findActiveById(UUID id);

    @Query("""
        SELECT s FROM School s
        WHERE s.archived = false
        AND (LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(s.address.city) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY s.name
        """)
    Page<School> searchActiveSchools(String search, Pageable pageable);

    Page<School> findByArchivedFalse(Pageable pageable);

    Page<School> findByArchivedTrue(Pageable pageable);

    Optional<School> findByIdAndArchivedFalse(UUID id);
}
