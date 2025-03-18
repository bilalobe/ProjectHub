package com.projecthub.base.project.infrastructure.persistence;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.project.infrastructure.repository.ProjectJpaRepository;
import com.projecthub.base.project.infrastructure.repository.ProjectRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public class JpaProjectRepository implements ProjectRepository {
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

    @Override
    public List<Project> findAll() {
        return this.jpaRepository.findAll();
    }

    @Override
    public void delete(Project entity) {
        this.jpaRepository.delete(entity);
    }

    @Override
    public boolean existsById(UUID id) {
        return this.jpaRepository.existsById(id);
    }

    @Override
    public List<Project> findByTeamId(UUID teamId) {
        return this.jpaRepository.findAllByTeamId(teamId);
    }

    @Override
    public List<Project> findByStatus(ProjectStatus status) {
        return this.jpaRepository.findByStatus(status);
    }

    @Override
    public boolean existsByName(String name) {
        return this.jpaRepository.existsByName(name);
    }

    @Override
    public List<Project> findOverdueProjects() {
        return this.jpaRepository.findOverdueProjects();
    }

    @Override
    public List<Project> findTemplates() {
        return this.jpaRepository.findTemplates();
    }

    @Override
    public long countActiveProjectsByTeam(UUID teamId) {
        return this.jpaRepository.countByTeamIdAndStatus(teamId, ProjectStatus.ACTIVE);
    }
}
