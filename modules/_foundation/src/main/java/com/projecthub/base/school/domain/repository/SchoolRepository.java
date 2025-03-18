package com.projecthub.base.school.domain.repository;

import com.projecthub.base.school.domain.entity.School;
import com.projecthub.base.shared.repository.common.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolRepository extends BaseRepository<School, UUID>, JpaSpecificationExecutor<School> {
    Page<School> findActiveSchools(Pageable pageable);
    
    Page<School> findArchivedSchools(Pageable pageable);
    
    Optional<School> findActiveById(UUID id);
    
    Page<School> searchActiveSchools(String search, Pageable pageable);
    
    Page<School> findByArchivedFalse(Pageable pageable);
    
    Page<School> findByArchivedTrue(Pageable pageable);
    
    Optional<School> findByIdAndArchivedFalse(UUID id);
    
    boolean existsByCode(String code);
    
    Page<School> findAll(Pageable pageable);
}
