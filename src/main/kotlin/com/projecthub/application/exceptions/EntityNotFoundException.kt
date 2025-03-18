package com.projecthub.application.exceptions

import java.util.UUID

/**
 * Exception thrown when an entity cannot be found.
 */
class EntityNotFoundException(
    val entityType: String,
    val id: UUID
) : ApplicationException("Entity of type $entityType with id $id not found") {
    override val code: String = "entity.not.found"
}
