package com.projecthub.base.project.infrastructure.repository;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    
    Page<Project> findByTeamId(UUID teamId, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Project> searchByNameOrDescription(@Param("query") String query);
    
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.components WHERE p.id = :id")
    Optional<Project> findByIdWithComponents(@Param("id") UUID id);
    
    @Query("SELECT p FROM Project p WHERE p.status = :status")
    List<Project> findByStatus(@Param("status") ProjectStatus status);
    
    @Query("SELECT COUNT(p) > 0 FROM Project p WHERE p.name = :name AND p.teamId = :teamId")
    boolean existsByNameAndTeamId(@Param("name") String name, @Param("teamId") UUID teamId);
    
    List<Project> findOverdueProjects();
    
    List<Project> findTemplates();
    
    long countActiveProjectsByTeam(UUID teamId);
}
