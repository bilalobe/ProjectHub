package com.projecthub.bootstrap.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.projecthub.core.application.project.port.`in`.ProjectManagementUseCase
import com.projecthub.core.application.project.port.`in`.ProjectQueryUseCase
import com.projecthub.core.application.project.port.out.DomainEventPublisher
import com.projecthub.core.application.project.port.out.ProjectRepository
import com.projecthub.core.application.project.service.ProjectManagementService
import com.projecthub.core.application.project.service.ProjectQueryService
import com.projecthub.infrastructure.persistence.project.jpa.ProjectJpaAdapter
import com.projecthub.infrastructure.persistence.project.jpa.ProjectJpaRepository
import com.projecthub.adapter.`in`.graphql.ProjectQueryResolver
import com.projecthub.adapter.`in`.graphql.ProjectMutationResolver

/**
 * Spring configuration class for the Project domain.
 * This class defines how components are wired together without components knowing
 * about each other directly, maintaining the clean architecture design.
 */
@Configuration
class ProjectConfiguration {

    /**
     * Creates and configures the ProjectManagementService bean.
     * Note how the core application service only depends on the domain's interfaces,
     * not on any concrete implementation.
     */
    @Bean
    fun projectManagementUseCase(
        projectRepository: ProjectRepository,
        eventPublisher: DomainEventPublisher
    ): ProjectManagementUseCase = ProjectManagementService(projectRepository, eventPublisher)

    /**
     * Creates and configures the ProjectQueryService bean.
     * This service implements the query use cases for projects.
     */
    @Bean
    fun projectQueryUseCase(projectRepository: ProjectRepository): ProjectQueryUseCase =
        ProjectQueryService(projectRepository)

    /**
     * Creates and configures the ProjectJpaAdapter bean.
     * This adapter implements the ProjectRepository interface required by the domain,
     * using the JPA repository provided by Spring Data JPA.
     */
    @Bean
    fun projectRepository(projectJpaRepository: ProjectJpaRepository): ProjectRepository =
        ProjectJpaAdapter(projectJpaRepository)

    /**
     * Creates and configures the ProjectQueryResolver bean.
     * This resolver handles GraphQL queries for projects.
     */
    @Bean
    fun projectQueryResolver(projectQueryUseCase: ProjectQueryUseCase): ProjectQueryResolver =
        ProjectQueryResolver(projectQueryUseCase)

    /**
     * Creates and configures the ProjectMutationResolver bean.
     * This resolver handles GraphQL mutations for projects.
     */
    @Bean
    fun projectMutationResolver(
        projectManagementUseCase: ProjectManagementUseCase,
        projectQueryUseCase: ProjectQueryUseCase
    ): ProjectMutationResolver = ProjectMutationResolver(projectManagementUseCase, projectQueryUseCase)
}
