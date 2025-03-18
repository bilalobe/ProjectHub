package com.projecthub.architecture

import com.projecthub.application.project.dto.CreateProjectCommand
import com.projecthub.application.project.port.`in`.ProjectManagementUseCase
import com.projecthub.core.application.workflow.port.`in`.WorkflowManagementUseCase
import com.projecthub.domain.project.event.ProjectCreatedEvent
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.timeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SpringBootTest
class FeatureIntegrationTest {

    @Autowired
    private lateinit var projectManagementUseCase: ProjectManagementUseCase

    @SpyBean
    private lateinit var projectEventListener: TestProjectEventListener

    @Test
    @Transactional
    fun `when project is created workflow is automatically created`() {
        // Given a project creation command
        val projectId = UUID.randomUUID().toString()
        val command = CreateProjectCommand(
            name = "Test Integration Project",
            description = "Project for testing feature integration",
            ownerId = "user-1"
        )

        // When project is created
        projectManagementUseCase.createProject(command)

        // Then the workflow module should receive the domain event
        verify(projectEventListener, timeout(1000)).handleProjectCreatedEvent(any(ProjectCreatedEvent::class.java))
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
 * Test component that listens for project events
 * This simulates the integration between project and workflow bounded contexts
 */
@Component
class TestProjectEventListener(@Autowired private val workflowManagementUseCase: WorkflowManagementUseCase) {

    // Event latch to help with asynchronous testing
    private val eventLatch = CountDownLatch(1)

    @EventListener
    fun handleProjectCreatedEvent(event: ProjectCreatedEvent) {
        // Create a workflow for the new project
        workflowManagementUseCase.createWorkflow("Default workflow for ${event.name}")
        eventLatch.countDown()
    }

    fun awaitEvent(timeout: Long = 2): Boolean {
        return eventLatch.await(timeout, TimeUnit.SECONDS)
    }
}
