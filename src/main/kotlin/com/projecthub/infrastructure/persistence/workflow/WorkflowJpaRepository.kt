package com.projecthub.infrastructure.persistence.workflow

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface WorkflowJpaRepository : JpaRepository<WorkflowJpaEntity, String> {
    fun findById(workflowId: String): Optional<WorkflowJpaEntity>
}
