package com.projecthub.infrastructure.persistence.workflow

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "workflow_definitions")
class WorkflowJpaEntity(
    @Id
    val id: String,
    val name: String
) {
    // Default constructor for JPA
    protected constructor() : this("", "")
}
