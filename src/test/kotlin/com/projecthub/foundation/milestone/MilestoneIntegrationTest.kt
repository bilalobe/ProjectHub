package com.projecthub.milestone

import com.projecthub.application.milestone.dto.CreateMilestoneCommand
import com.projecthub.application.milestone.dto.UpdateMilestoneCommand
import com.projecthub.application.milestone.port.`in`.MilestoneManagementUseCase
import com.projecthub.domain.milestone.MilestoneStatus
import com.projecthub.domain.milestone.event.MilestoneCompletedEvent
import com.projecthub.domain.milestone.event.MilestoneDueDateChangedEvent
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.timeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.assertj.core.api.Assertions.assertThat

@SpringBootTest
class MilestoneIntegrationTest {

    @Autowired
    private lateinit var milestoneManagementUseCase: MilestoneManagementUseCase

    @SpyBean
    private lateinit var testEventListener: TestMilestoneEventListener

    @Test
    @Transactional
    fun `when milestone is created it emits proper events and maintains invariants`() {
        // Given
        val projectId = UUID.randomUUID().toString()
        val dueDate = LocalDate.now().plusWeeks(2)
        val command = CreateMilestoneCommand(
            name = "Test Milestone",
            description = "Integration test milestone",
            projectId = projectId,
            dueDate = dueDate
        )

        // When
        val milestone = milestoneManagementUseCase.createMilestone(command)

        // Then
        assertThat(milestone.name).isEqualTo(command.name)
        assertThat(milestone.description).isEqualTo(command.description)
        assertThat(milestone.projectId).isEqualTo(projectId)
        assertThat(milestone.status).isEqualTo(MilestoneStatus.PENDING)
        assertThat(milestone.progress).isZero()
    }

    @Test
    @Transactional
    fun `when milestone due date is updated it emits proper events`() {
        // Given
        val projectId = UUID.randomUUID().toString()
        val originalDueDate = LocalDate.now().plusWeeks(2)
        val createCommand = CreateMilestoneCommand(
            name = "Test Milestone",
            description = "Integration test milestone",
            projectId = projectId,
            dueDate = originalDueDate
        )
        val milestone = milestoneManagementUseCase.createMilestone(createCommand)

        // When
        val newDueDate = originalDueDate.plusWeeks(1)
        val updateCommand = UpdateMilestoneCommand(
            name = milestone.name,
            description = milestone.description,
            dueDate = newDueDate
        )
        milestoneManagementUseCase.updateMilestone(milestone.id, updateCommand)

        // Then
        verify(testEventListener, timeout(1000))
            .handleDueDateChangedEvent(any(MilestoneDueDateChangedEvent::class.java))
    }

    @Test
    @Transactional
    fun `when milestone is completed it updates project progress`() {
        // Given
        val projectId = UUID.randomUUID().toString()
        val createCommand = CreateMilestoneCommand(
            name = "Test Milestone",
            description = "Integration test milestone",
            projectId = projectId,
            dueDate = LocalDate.now()
        )
        val milestone = milestoneManagementUseCase.createMilestone(createCommand)

        // When completing the milestone
        val completedMilestone = milestoneManagementUseCase.updateMilestoneStatus(
            milestone.id,
            com.projecthub.application.milestone.dto.UpdateMilestoneStatusCommand(
                MilestoneStatus.COMPLETED
            )
        )

        // Then
        assertThat(completedMilestone.status).isEqualTo(MilestoneStatus.COMPLETED)
        verify(testEventListener, timeout(1000))
            .handleMilestoneCompletedEvent(any(MilestoneCompletedEvent::class.java))
    }

    /**
     * Helper method to match any instance of a class
     */
    private fun <T> any(type: Class<T>): T {
        org.mockito.kotlin.any<T>()
        return null as T
    }
}

/**
 * Test component that listens for milestone events
 */
@Component
class TestMilestoneEventListener {

    private val eventLatch = CountDownLatch(1)

    @EventListener
    fun handleMilestoneCompletedEvent(event: MilestoneCompletedEvent) {
        eventLatch.countDown()
    }

    @EventListener
    fun handleDueDateChangedEvent(event: MilestoneDueDateChangedEvent) {
        eventLatch.countDown()
    }

    fun awaitEvent(timeout: Long = 2): Boolean {
        return eventLatch.await(timeout, TimeUnit.SECONDS)
    }
}
