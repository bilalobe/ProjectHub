package com.projecthub.core.repositories.jpa;

import com.projecthub.core.models.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Component} entities.
 */
@Repository("jpaComponentRepository")
@Profile("jpa")
public interface ComponentJpaRepository extends JpaRepository<Component, UUID> {

    /**
     * Finds components by project ID.
     *
     * @param projectId the UUID of the project
     * @return a list of {@code Component} objects belonging to the project
     */
    List<Component> findByProjectId(UUID projectId);
}