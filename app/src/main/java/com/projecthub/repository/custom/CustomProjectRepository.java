package com.projecthub.repository.custom;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecthub.dto.ProjectSummary;
import com.projecthub.model.Project;

@Repository
public interface CustomProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByTeamId(Long teamId);
    List<Project> findAllByUserId(Long userId);
    Optional<Project> findProjectWithComponentsById(Long projectId);
    public Project save(ProjectSummary projectSummary);
}