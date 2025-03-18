package com.projecthub.application.cohort.service

import com.projecthub.application.cohort.port.`in`.CohortManagementUseCase
import com.projecthub.application.cohort.dto.CohortDTO
import com.projecthub.application.cohort.dto.CreateCohortCommand
import com.projecthub.application.cohort.dto.UpdateCohortCommand
import com.projecthub.application.cohort.dto.AssignStudentCommand
import com.projecthub.application.cohort.mapper.CohortMapper
import com.projecthub.domain.cohort.Cohort
import com.projecthub.domain.cohort.CohortRepository
import com.projecthub.domain.event.DomainEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Implementation of the CohortManagementUseCase interface
 * This service orchestrates cohort operations between domain and infrastructure layers
 */
@Service
class CohortManagementService(
    private val cohortRepository: CohortRepository,
    private val cohortMapper: CohortMapper,
    private val eventPublisher: DomainEventPublisher
) : CohortManagementUseCase {

    @Transactional
    override fun createCohort(command: CreateCohortCommand): CohortDTO {
        val cohortId = UUID.randomUUID().toString()

        val cohort = Cohort.create(
            id = cohortId,
            name = command.name,
            code = command.code,
            schoolId = command.schoolId,
            capacity = command.capacity,
            startTerm = command.startDate,
            endTerm = command.endDate ?: command.startDate.plusMonths(6)
        )

        val savedCohort = cohortRepository.save(cohort)

        // Publish domain events
        cohort.domainEvents.forEach { eventPublisher.publish(it) }
        cohort.clearEvents()

        return cohortMapper.toDto(savedCohort)
    }

    @Transactional
    override fun updateCohort(cohortId: String, command: UpdateCohortCommand): CohortDTO {
        val cohort = cohortRepository.findById(cohortId)
            ?: throw IllegalArgumentException("Cohort not found with ID: $cohortId")

        cohort.updateDetails(
            name = command.name,
            capacity = command.capacity,
            startTerm = command.startDate,
            endTerm = command.endDate ?: command.startDate.plusMonths(6)
        )

        val updatedCohort = cohortRepository.save(cohort)

        // Publish domain events
        cohort.domainEvents.forEach { eventPublisher.publish(it) }
        cohort.clearEvents()

        return cohortMapper.toDto(updatedCohort)
    }

    @Transactional
    override fun assignStudentToCohort(command: AssignStudentCommand): CohortDTO {
        val cohort = cohortRepository.findById(command.cohortId)
            ?: throw IllegalArgumentException("Cohort not found with ID: ${command.cohortId}")

        cohort.assignStudent(command.studentId)

        val updatedCohort = cohortRepository.save(cohort)

        // Publish domain events
        cohort.domainEvents.forEach { eventPublisher.publish(it) }
        cohort.clearEvents()

        return cohortMapper.toDto(updatedCohort)
    }

    @Transactional
    override fun completeCohort(cohortId: String): CohortDTO {
        val cohort = cohortRepository.findById(cohortId)
            ?: throw IllegalArgumentException("Cohort not found with ID: $cohortId")

        cohort.complete()

        val updatedCohort = cohortRepository.save(cohort)

        // Publish domain events
        cohort.domainEvents.forEach { eventPublisher.publish(it) }
        cohort.clearEvents()

        return cohortMapper.toDto(updatedCohort)
    }

    @Transactional(readOnly = true)
    override fun getCohortById(cohortId: String): CohortDTO? {
        return cohortRepository.findById(cohortId)?.let { cohortMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getAllCohorts(): List<CohortDTO> {
        return cohortRepository.findAll().map { cohortMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getCohortsBySchool(schoolId: String): List<CohortDTO> {
        return cohortRepository.findBySchool(schoolId).map { cohortMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getActiveCohortsWithCapacity(minCapacity: Int): List<CohortDTO> {
        return cohortRepository.findActiveCohortsWithCapacity(minCapacity)
            .map { cohortMapper.toDto(it) }
    }
}
