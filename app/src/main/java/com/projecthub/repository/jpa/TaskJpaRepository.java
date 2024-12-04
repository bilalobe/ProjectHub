package com.projecthub.repository.jpa;

import com.projecthub.model.Task;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for {@link Task} entities.
 */
@Repository("jpaTaskRepository")
@Profile("jpa")
public interface TaskJpaRepository extends JpaRepository<Task, UUID> {
    // Add methods with UUID parameters and appropriate docstrings
}