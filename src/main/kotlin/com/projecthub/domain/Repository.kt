package com.projecthub.domain

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.util.*

@NoRepositoryBean
interface Repository<T : AggregateRoot<*>> : Repository<T, UUID> {
    suspend fun findById(id: UUID): T?
    suspend fun save(entity: T): T
    suspend fun delete(entity: T)
    suspend fun exists(id: UUID): Boolean
}
