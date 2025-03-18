package com.projecthub.milestone

import com.projecthub.application.milestone.dto.CreateMilestoneCommand
import com.projecthub.application.milestone.dto.UpdateMilestoneStatusCommand
import com.projecthub.application.milestone.port.`in`.MilestoneManagementUseCase
import com.projecthub.application.project.port.`in`.ProjectManagementUseCase
import com.projecthub.application.task.port.`in`.TaskManagementUseCase
import com.projecthub.domain.milestone.MilestoneStatus
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.transaction.TestTransaction
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID
import org.mockito.kotlin.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.timeout

@SpringBootTest
class MilestoneCrossCuttingIntegrationTest {

    @Autowired
    private lateinit var milestoneManagementUseCase: MilestoneManagementUseCase

    @MockBean
    private lateinit var projectManagementUseCase: ProjectManagementUseCase

    @MockBean
    private lateinit var taskManagementUseCase: TaskManagementUseCase

    @Test
    @Transactional
    fun `when milestone is completed, project progress is recalculated and tasks are updated`() {
        // Given
        val projectId = UUID.randomUUID().toString()
        val milestone = milestoneManagementUseCase.createMilestone(
            CreateMilestoneCommand(
                name = "Integration Test Milestone",
                description = "Testing cross-feature interactions",
                projectId = projectId,
                dueDate = LocalDate.now().plusDays(7)
            )
        )

        // When - Complete the milestone
        TestTransaction.flagForCommit()
        val completedMilestone = milestoneManagementUseCase.updateMilestoneStatus(
            milestone.id,
            UpdateMilestoneStatusCommand(MilestoneStatus.COMPLETED)
        )
        TestTransaction.end()

        // Then - Verify cross-feature interactions
        verify(projectManagementUseCase, timeout(1000))
            .recalculateProgress(projectId)

        verify(taskManagementUseCase, timeout(1000))
            .completeTasksForMilestone(milestone.id)
    }

    @Test
    @Transactional
    fun `when milestone due date changes, dependent tasks are updated`() {
        // Given
        val projectId = UUID.randomUUID().toString()
        val milestone = milestoneManagementUseCase.createMilestone(
            CreateMilestoneCommand(
                name = "Integration Test Milestone",
                description = "Testing cross-feature interactions",
                projectId = projectId,
                dueDate = LocalDate.now().plusDays(7)
            )
        )

        // When - Update the due date
        TestTransaction.flagForCommit()
        val updatedMilestone = milestoneManagementUseCase.updateMilestone(
            milestone.id,
            com.projecthub.application.milestone.dto.UpdateMilestoneCommand(
                name = milestone.name,
                description = milestone.description,
                dueDate = LocalDate.now().plusDays(14)
            )
        )
        TestTransaction.end()

        // Then - Verify task dates are updated
        verify(taskManagementUseCase, timeout(1000))
            .updateTaskDatesForMilestone(
                eq(milestone.id),
                any(),
                any()
            )
    }

    @Test
    @Transactional
    fun `when milestone is assigned, unassigned tasks are reassigned`() {
        // Given
        val projectId = UUID.randomUUID().toString()
        val assigneeId = UUID.randomUUID().toString()
        val milestone = milestoneManagementUseCase.createMilestone(
            CreateMilestoneCommand(
                name = "Integration Test Milestone",
                description = "Testing cross-feature interactions",
                projectId = projectId,
                dueDate = LocalDate.now().plusDays(7)
            )
        )

        // When - Assign the milestone
        TestTransaction.flagForCommit()
        val assignedMilestone = milestoneManagementUseCase.assignMilestone(
            milestone.id,
            com.projecthub.application.milestone.dto.AssignMilestoneCommand(
                assigneeId = assigneeId
            )
        )
        TestTransaction.end()

        // Then - Verify unassigned tasks are reassigned
        verify(taskManagementUseCase, timeout(1000))
            .reassignUnassignedTasksForMilestone(
                eq(milestone.id),
                eq(assigneeId)
            )
    }

    private fun <T> eq(value: T): T = org.mockito.kotlin.eq(value) ?: value
}
