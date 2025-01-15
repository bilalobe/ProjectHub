package com.projecthub.base.project.infrastructure.persistence;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.infrastructure.port.out.ProjectStoragePort;
import com.projecthub.base.project.infrastructure.repository.ProjectJpaRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public class JpaProjectRepository implements ProjectStoragePort {
    private final ProjectJpaRepository jpaRepository;

    public JpaProjectRepository(final ProjectJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Project save(final Project project) {
        return this.jpaRepository.save(project);
    }

    @Override
    public Optional<Project> findById(final UUID id) {
        return this.jpaRepository.findById(id);
    }
}
