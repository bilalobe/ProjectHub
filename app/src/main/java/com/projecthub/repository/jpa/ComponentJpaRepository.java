package com.projecthub.repository.jpa;

import com.projecthub.model.Component;

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
public interface ComponentJpaRepository<PK> extends JpaRepository<Component, UUID> {

    /**
     * Finds components by project ID.
     *
     * @param project_id the UUID of the project
     * @return a list of {@code Component} objects belonging to the project
     */
    List<Component> findByProjectId(PK project_id);
}