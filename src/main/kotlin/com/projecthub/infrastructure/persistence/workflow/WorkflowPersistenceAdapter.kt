package com.projecthub.infrastructure.persistence.workflow

import com.projecthub.application.workflow.mapper.WorkflowMapper
import com.projecthub.domain.workflow.WorkflowRepository
import com.projecthub.domain.workflow.WorkflowDefinition
import com.projecthub.domain.workflow.WorkflowState
import org.springframework.stereotype.Component

/**
 * Adapter implementing the WorkflowRepository port from domain layer
 * This class bridges the domain model with the database through JPA
 */
@Component
class WorkflowPersistenceAdapter(
    private val workflowJpaRepository: WorkflowJpaRepository,
    private val workflowMapper: WorkflowMapper
) : WorkflowRepository {

    override fun findById(id: String): WorkflowDefinition? {
        return workflowJpaRepository.findById(id)
            .map { workflowMapper.mapToDomain(it) }
            .orElse(null)
    }

    override fun save(workflow: WorkflowDefinition): WorkflowDefinition {
        val entity = workflowMapper.mapToEntity(workflow)
        val savedEntity = workflowJpaRepository.save(entity)
        return workflowMapper.mapToDomain(savedEntity)
    }

    override fun findAll(): List<WorkflowDefinition> {
        return workflowJpaRepository.findAll().map { workflowMapper.mapToDomain(it) }
    }

    override fun findByState(state: WorkflowState): List<WorkflowDefinition> {
        return workflowJpaRepository.findByCurrentState(state.name)
            .map { workflowMapper.mapToDomain(it) }
    }
}
