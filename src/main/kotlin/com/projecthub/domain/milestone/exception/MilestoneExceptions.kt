package com.projecthub.domain.milestone.exception

/**
 * Base exception for milestone-related errors
 */
sealed class MilestoneException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)

/**
 * Thrown when attempting to modify a completed milestone
 */
class CompletedMilestoneModificationException(milestoneId: String) :
    MilestoneException("Cannot modify completed milestone: $milestoneId")

/**
 * Thrown when attempting an invalid status transition
 */
class InvalidMilestoneStatusTransitionException(
    currentStatus: String,
    attemptedStatus: String
) : MilestoneException("Invalid status transition from $currentStatus to $attemptedStatus")

/**
 * Thrown when a cyclic dependency is detected
 */
class CyclicDependencyException(milestoneId: String, dependencyId: String) :
    MilestoneException("Adding dependency $dependencyId to milestone $milestoneId would create a cycle")

/**
 * Thrown when milestone due date conflicts with dependencies
 */
class DependencyDueDateConflictException(message: String) :
    MilestoneException(message)

/**
 * Thrown when preconditions for milestone completion are not met
 */
class MilestoneCompletionPreConditionException(message: String) :
    MilestoneException(message)

/**
 * Thrown when milestone capacity or assignment constraints are violated
 */
class MilestoneConstraintViolationException(message: String) :
    MilestoneException(message)
