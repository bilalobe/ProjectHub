package com.projecthub.base.milestone.infrastructure.persistence;

import com.projecthub.base.milestone.domain.entity.Milestone;
import com.projecthub.base.milestone.domain.enums.MilestoneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, UUID>, JpaSpecificationExecutor<Milestone> {
    @Query("SELECT m FROM Milestone m WHERE m.project.id = :projectId AND m.status = :status")
    List<Milestone> findByProjectIdAndStatus(UUID projectId, MilestoneStatus status);

    @Query("SELECT m FROM Milestone m WHERE m.dueDate < :date AND m.status = 'PENDING'")
    List<Milestone> findOverdueMilestones(LocalDate date);

    boolean existsByProjectIdAndName(UUID projectId, String name);

    @Query("SELECT m FROM Milestone m WHERE m.project.id = :projectId ORDER BY m.dueDate ASC")
    List<Milestone> findByProjectIdOrderByDueDateAsc(UUID projectId);

    @Query("SELECT COUNT(m) FROM Milestone m WHERE m.project.id = :projectId AND m.status = :status")
    long countByProjectIdAndStatus(UUID projectId, MilestoneStatus status);

    @Query("SELECT m FROM Milestone m WHERE m.project.id = :projectId AND m.dueDate BETWEEN :startDate AND :endDate")
    List<Milestone> findByProjectIdAndDueDateBetween(UUID projectId, LocalDate startDate, LocalDate endDate);

    Arrays findByProjectId(UUID projectId);
}
