package com.projecthub.base.school.infrastructure.persistence.jpa;

import com.projecthub.base.school.domain.entity.School;
import com.projecthub.base.shared.repository.common.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolJpaRepository extends BaseRepository<School, UUID>, JpaSpecificationExecutor<School> {
    Optional<School> findByIdAndArchivedFalse(UUID id);

    Page<School> findByArchivedFalse(Pageable pageable);

    Page<School> findByArchivedTrue(Pageable pageable);

    boolean existsByCode(String code);

    @Query("""
        SELECT s FROM School s
        WHERE s.archived = false
        AND (LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(s.address.city) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY s.name
        """)
    Page<School> searchActiveSchools(String search, Pageable pageable);

    Page<School> findAll(Pageable pageable);
}
