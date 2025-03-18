package com.projecthub.domain.milestone.validation

import com.projecthub.domain.milestone.Milestone
import com.projecthub.domain.milestone.MilestoneStatus
import com.projecthub.domain.milestone.exception.*
import com.projecthub.domain.project.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import org.assertj.core.api.Assertions.assertThatCode

class MilestoneValidationServiceTest {

    private lateinit var validationService: MilestoneValidationService
    private lateinit var mockProject: Project
    private lateinit var mockMilestone: Milestone

    @BeforeEach
    fun setup() {
        validationService = MilestoneValidationService()
        mockProject = mock()
        mockMilestone = mock()

        // Setup default mock behavior
        whenever(mockMilestone.id).thenReturn("milestone-1")
        whenever(mockMilestone.isCompleted).thenReturn(false)
        whenever(mockMilestone.status).thenReturn(MilestoneStatus.PENDING)
        whenever(mockMilestone.dependencies).thenReturn(emptySet())
        whenever(mockMilestone.tasks).thenReturn(emptySet())
    }

    @Test
    fun `validateCreation should pass with valid inputs`() {
        assertThatCode {
            validationService.validateCreation(
                name = "Test Milestone",
                description = "Test Description",
                dueDate = LocalDate.now().plusDays(1)
            )
        }.doesNotThrowAnyException()
    }

    @Test
    fun `validateCreation should fail with empty name`() {
        assertThrows<IllegalArgumentException> {
            validationService.validateCreation(
                name = "",
                description = "Test Description",
                dueDate = LocalDate.now().plusDays(1)
            )
        }
    }

    @Test
    fun `validateCreation should fail with past due date`() {
        assertThrows<IllegalArgumentException> {
            validationService.validateCreation(
                name = "Test Milestone",
                description = "Test Description",
                dueDate = LocalDate.now().minusDays(1)
            )
        }
    }

    @Test
    fun `validateStatusTransition should fail for completed milestone`() {
        whenever(mockMilestone.isCompleted).thenReturn(true)

        assertThrows<CompletedMilestoneModificationException> {
            validationService.validateStatusTransition(
                milestone = mockMilestone,
                newStatus = MilestoneStatus.IN_PROGRESS
            )
        }
    }

    @Test
    fun `validateStatusTransition to COMPLETED should check dependencies`() {
        val incompleteDependency = mock<Milestone>()
        whenever(incompleteDependency.isCompleted).thenReturn(false)
        whenever(mockMilestone.dependencies).thenReturn(setOf(incompleteDependency))

        assertThrows<MilestoneCompletionPreConditionException> {
            validationService.validateStatusTransition(
                milestone = mockMilestone,
                newStatus = MilestoneStatus.COMPLETED
            )
        }
    }

    @Test
    fun `validateDueDateChange should fail for past date`() {
        assertThrows<MilestoneConstraintViolationException> {
            validationService.validateDueDateChange(
                milestone = mockMilestone,
                newDueDate = LocalDate.now().minusDays(1)
            )
        }
    }

    @Test
    fun `validateDueDateChange should check dependency constraints`() {
        val dependency = mock<Milestone>()
        whenever(dependency.id).thenReturn("dep-1")
        whenever(dependency.dueDate).thenReturn(LocalDate.now().plusDays(5))
        whenever(mockMilestone.dependencies).thenReturn(setOf(dependency))

        assertThrows<DependencyDueDateConflictException> {
            validationService.validateDueDateChange(
                milestone = mockMilestone,
                newDueDate = LocalDate.now().plusDays(3) // Earlier than dependency
            )
        }
    }

    @Test
    fun `validateDependencyAddition should detect cycles`() {
        val dependency = mock<Milestone>()
        whenever(dependency.id).thenReturn("milestone-1") // Same as mock milestone
        whenever(dependency.dependencies).thenReturn(emptySet())

        assertThrows<CyclicDependencyException> {
            validationService.validateDependencyAddition(
                milestone = mockMilestone,
                dependency = dependency
            )
        }
    }

    @Test
    fun `validateDependencyAddition should check due dates`() {
        val dependency = mock<Milestone>()
        whenever(dependency.id).thenReturn("dep-1")
        whenever(dependency.dueDate).thenReturn(LocalDate.now().plusDays(10))
        whenever(mockMilestone.dueDate).thenReturn(LocalDate.now().plusDays(5))
        whenever(dependency.dependencies).thenReturn(emptySet())

        assertThrows<DependencyDueDateConflictException> {
            validationService.validateDependencyAddition(
                milestone = mockMilestone,
                dependency = dependency
            )
        }
    }
}
