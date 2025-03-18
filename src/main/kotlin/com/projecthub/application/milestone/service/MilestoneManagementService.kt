package com.projecthub.application.milestone.service

import com.projecthub.application.milestone.dto.*
import com.projecthub.application.milestone.mapper.MilestoneMapper
import com.projecthub.application.milestone.port.`in`.MilestoneManagementUseCase
import com.projecthub.domain.event.DomainEventPublisher
import com.projecthub.domain.milestone.Milestone
import com.projecthub.domain.milestone.MilestoneRepository
import com.projecthub.domain.project.ProjectRepository
import com.projecthub.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
class MilestoneManagementService(
    private val milestoneRepository: MilestoneRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    private val milestoneMapper: MilestoneMapper,
    private val eventPublisher: DomainEventPublisher
) : MilestoneManagementUseCase {

    @Transactional
    override fun createMilestone(command: CreateMilestoneCommand): MilestoneDTO {
        val project = projectRepository.findById(command.projectId)
            ?: throw IllegalArgumentException("Project not found with ID: ${command.projectId}")

        val milestone = Milestone.create(
            id = UUID.randomUUID().toString(),
            name = command.name,
            description = command.description,
            project = project,
            dueDate = command.dueDate
        )

        // Assign if specified
        command.assigneeId?.let { assigneeId ->
            val user = userRepository.findById(assigneeId)
                ?: throw IllegalArgumentException("User not found with ID: $assigneeId")
            milestone.assignTo(user)
        }

        // Add dependencies if specified
        command.dependencies.forEach { dependencyId ->
            val dependency = milestoneRepository.findById(dependencyId)
                ?: throw IllegalArgumentException("Dependency milestone not found with ID: $dependencyId")
            milestone.addDependency(dependency)
        }

        val savedMilestone = milestoneRepository.save(milestone)

        // Publish domain events
        milestone.domainEvents.forEach { eventPublisher.publish(it) }
        milestone.clearEvents()

        return milestoneMapper.toDto(savedMilestone)
    }

    @Transactional
    override fun updateMilestone(milestoneId: String, command: UpdateMilestoneCommand): MilestoneDTO {
        val milestone = milestoneRepository.findById(milestoneId)
            ?: throw IllegalArgumentException("Milestone not found with ID: $milestoneId")

        milestone.updateDetails(
            name = command.name,
            description = command.description,
            dueDate = command.dueDate
        )

        val updatedMilestone = milestoneRepository.save(milestone)

        milestone.domainEvents.forEach { eventPublisher.publish(it) }
        milestone.clearEvents()

        return milestoneMapper.toDto(updatedMilestone)
    }

    @Transactional
    override fun updateMilestoneStatus(milestoneId: String, command: UpdateMilestoneStatusCommand): MilestoneDTO {
        val milestone = milestoneRepository.findById(milestoneId)
            ?: throw IllegalArgumentException("Milestone not found with ID: $milestoneId")

        milestone.updateStatus(command.newStatus)

        val updatedMilestone = milestoneRepository.save(milestone)

        milestone.domainEvents.forEach { eventPublisher.publish(it) }
        milestone.clearEvents()

        return milestoneMapper.toDto(updatedMilestone)
    }

    @Transactional
    override fun assignMilestone(milestoneId: String, command: AssignMilestoneCommand): MilestoneDTO {
        val milestone = milestoneRepository.findById(milestoneId)
            ?: throw IllegalArgumentException("Milestone not found with ID: $milestoneId")

        val assignee = userRepository.findById(command.assigneeId)
            ?: throw IllegalArgumentException("User not found with ID: ${command.assigneeId}")

        milestone.assignTo(assignee)

        val updatedMilestone = milestoneRepository.save(milestone)

        milestone.domainEvents.forEach { eventPublisher.publish(it) }
        milestone.clearEvents()

        return milestoneMapper.toDto(updatedMilestone)
    }

    @Transactional
    override fun addDependency(milestoneId: String, command: AddDependencyCommand): MilestoneDTO {
        val milestone = milestoneRepository.findById(milestoneId)
            ?: throw IllegalArgumentException("Milestone not found with ID: $milestoneId")

        val dependency = milestoneRepository.findById(command.dependencyId)
            ?: throw IllegalArgumentException("Dependency milestone not found with ID: ${command.dependencyId}")

        milestone.addDependency(dependency)

        val updatedMilestone = milestoneRepository.save(milestone)

        return milestoneMapper.toDto(updatedMilestone)
    }

    @Transactional(readOnly = true)
    override fun getMilestoneById(milestoneId: String): MilestoneDTO? {
        return milestoneRepository.findById(milestoneId)?.let { milestoneMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getProjectMilestones(projectId: String): List<MilestoneDTO> {
        return milestoneRepository.findByProjectId(projectId).map { milestoneMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getAssignedMilestones(assigneeId: String): List<MilestoneDTO> {
        return milestoneRepository.findByAssigneeId(assigneeId).map { milestoneMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getUpcomingMilestones(date: LocalDate): List<MilestoneDTO> {
        return milestoneRepository.findUpcomingMilestones(date).map { milestoneMapper.toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getOverdueMilestones(date: LocalDate): List<MilestoneDTO> {
        return milestoneRepository.findOverdueMilestones(date).map { milestoneMapper.toDto(it) }
    }
}
