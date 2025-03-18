package com.projecthub.infrastructure.persistence.cohort

import com.projecthub.application.cohort.mapper.CohortMapper
import com.projecthub.domain.cohort.Cohort
import com.projecthub.domain.cohort.CohortRepository
import com.projecthub.infrastructure.persistence.cohort.jpa.CohortJpaRepository
import org.springframework.stereotype.Component

/**
 * Adapter implementing the CohortRepository interface
 * This adapter connects the domain layer to the persistence infrastructure
 */
@Component
class CohortPersistenceAdapter(
    private val cohortJpaRepository: CohortJpaRepository,
    private val cohortMapper: CohortMapper
) : CohortRepository {

    override fun findById(id: String): Cohort? {
        return cohortJpaRepository.findById(id)
            .map { cohortMapper.toDomainEntity(it) }
            .orElse(null)
    }

    override fun findAll(): List<Cohort> {
        return cohortJpaRepository.findAll().map { cohortMapper.toDomainEntity(it) }
    }

    override fun findBySchool(schoolId: String): List<Cohort> {
        return cohortJpaRepository.findBySchoolId(schoolId).map { cohortMapper.toDomainEntity(it) }
    }

    override fun save(cohort: Cohort): Cohort {
        val jpaEntity = cohortMapper.toJpaEntity(cohort)
        val savedEntity = cohortJpaRepository.save(jpaEntity)
        return cohortMapper.toDomainEntity(savedEntity)
    }

    override fun deleteById(id: String) {
        cohortJpaRepository.deleteById(id)
    }

    override fun findActiveCohortsWithCapacity(minCapacity: Int): List<Cohort> {
        return cohortJpaRepository.findActiveCohortsWithCapacity(minCapacity = minCapacity)
            .map { cohortMapper.toDomainEntity(it) }
    }
}
