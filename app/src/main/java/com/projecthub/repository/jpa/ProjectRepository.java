package com.projecthub.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projecthub.model.Project;
import com.projecthub.repository.custom.CustomProjectRepository;

@Repository("postgresProjectRepository")
public interface ProjectRepository extends JpaRepository<Project, Long>, CustomProjectRepository {
    @Override
    List<Project> findAllByTeamId(Long teamId);

    @Override
    @Query("SELECT p FROM Project p JOIN FETCH p.components WHERE p.id = :projectId")
    Project findProjectWithComponentsById(Long projectId);
}