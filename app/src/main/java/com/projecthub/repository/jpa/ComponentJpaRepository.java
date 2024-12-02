package com.projecthub.repository.jpa;

import com.projecthub.model.Component;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link Component} entities.
 */
@Repository("jpaComponentRepository")
@Profile("jpa")
public interface ComponentJpaRepository extends JpaRepository<Component, Long> {

    /**
     * Finds components by project ID.
     *
     * @param projectId the ID of the project
     * @return a list of {@code Component} objects belonging to the project
     */
    List<Component> findByProjectId(Long projectId);
}