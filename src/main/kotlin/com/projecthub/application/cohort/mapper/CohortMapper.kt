package com.projecthub.application.cohort.mapper

import com.projecthub.application.cohort.dto.CohortDTO
import com.projecthub.domain.cohort.Cohort
import com.projecthub.infrastructure.persistence.cohort.jpa.CohortJpaEntity
import org.springframework.stereotype.Component

/**
 * Mapper for converting between Cohort domain entities and DTOs/persistence entities
 */
@Component
class CohortMapper {

    /**
     * Maps domain entity to DTO
     */
    fun toDto(cohort: Cohort): CohortDTO {
        return CohortDTO(
            id = cohort.id,
            name = cohort.name,
            code = cohort.code,
            schoolId = cohort.schoolId,
            capacity = cohort.assignment.maxStudents,
            currentEnrollment = cohort.currentEnrollment,
            startDate = cohort.startTerm,
            endDate = cohort.endTerm,
            isActive = cohort.isActive,
            createdAt = cohort.createdAt,
            updatedAt = cohort.updatedAt
        )
    }

    /**
     * Maps domain entity to JPA entity
     */
    fun toJpaEntity(cohort: Cohort): CohortJpaEntity {
        return CohortJpaEntity(
            id = cohort.id,
            name = cohort.name,
            code = cohort.code,
            schoolId = cohort.schoolId,
            maxStudents = cohort.assignment.maxStudents,
            maxTeams = cohort.assignment.maxTeams,
            startTerm = cohort.startTerm,
            endTerm = cohort.endTerm,
            isArchived = cohort.isArchived,
            archiveReason = cohort.archiveReason,
            createdAt = cohort.createdAt,
            updatedAt = cohort.updatedAt
        )
    }

    /**
     * Maps JPA entity to domain entity
     * Note: This uses reflection for demo purposes; in production,
     * consider creating appropriate factory methods in the domain class
     */
    fun toDomainEntity(entity: CohortJpaEntity): Cohort {
        val cohortClass = Cohort::class.java

        // Access private constructor using reflection
        val constructor = cohortClass.getDeclaredConstructor(
            String::class.java,             // id
            String::class.java,             // name
            String::class.java,             // code
            String::class.java,             // schoolId
            com.projecthub.domain.cohort.CohortAssignment::class.java, // assignment
            java.time.LocalDate::class.java, // startTerm
            java.time.LocalDate::class.java, // endTerm
            Boolean::class.java,            // isArchived
            String::class.java,             // archiveReason
            com.projecthub.domain.cohort.SeatingConfiguration::class.java, // seatingConfig
            java.time.LocalDateTime::class.java, // createdAt
            java.time.LocalDateTime::class.java  // updatedAt
        )

        constructor.isAccessible = true

        return constructor.newInstance(
            entity.id,
            entity.name,
            entity.code,
            entity.schoolId,
            com.projecthub.domain.cohort.CohortAssignment(
                maxStudents = entity.maxStudents,
                maxTeams = entity.maxTeams
            ),
            entity.startTerm,
            entity.endTerm,
            entity.isArchived,
            entity.archiveReason,
            null, // seatingConfig
            entity.createdAt,
            entity.updatedAt
        )
    }
}
