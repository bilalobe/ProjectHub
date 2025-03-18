package com.projecthub.base.submission.infrastructure.persistence;

import com.projecthub.base.submission.domain.entity.Submission;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionJpaRepository extends JpaRepository<Submission, UUID>, JpaSpecificationExecutor<Submission> {
   Page<Submission> findByStudentId(UUID studentId, Pageable pageable);
   Page<Submission> findByProjectId(UUID projectId, Pageable pageable);

   @Query("SELECT s FROM Submission s WHERE s.deadline < :now AND s.status = :status")
   List<Submission> findOverdueSubmissions();

   @Lock(LockModeType.PESSIMISTIC_WRITE)
   Optional<Submission> findByIdForUpdate(UUID id);
}
