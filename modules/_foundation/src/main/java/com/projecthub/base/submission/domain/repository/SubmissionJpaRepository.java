package com.projecthub.base.submission.domain.repository;

import com.projecthub.base.submission.domain.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionJpaRepository extends JpaRepository<Submission, UUID> {
    Optional<Submission> findBySubmissionId(UUID submissionId);
    
    List<Submission> findByStudentIdOrderBySubmittedAtDesc(UUID studentId);
    
    List<Submission> findByProjectIdOrderBySubmittedAtDesc(UUID projectId);
    
    @Query("SELECT s FROM Submission s WHERE s.projectId = ?1 AND s.status = 'SUBMITTED'")
    List<Submission> findPendingGradingByProjectId(UUID projectId);
    
    @Query("SELECT COUNT(s) > 0 FROM Submission s WHERE s.studentId = ?1 AND s.projectId = ?2 AND s.status NOT IN ('REVOKED')")
    boolean hasValidSubmissionForProject(UUID studentId, UUID projectId);
}