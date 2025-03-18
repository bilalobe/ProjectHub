package com.projecthub.domain.security.authorization

import com.projecthub.domain.security.permission.Permission
import com.projecthub.domain.security.rbac.RBACService
import com.projecthub.domain.project.ProjectRepository
import com.projecthub.domain.team.TeamRepository
import com.projecthub.domain.user.UserId
import org.springframework.stereotype.Service

/**
 * Service that enforces application-specific authorization rules.
 * This extends generic RBAC with domain-specific security logic.
 */
@Service
class DomainAuthorizationService(
    private val rbacService: RBACService,
    private val projectRepository: ProjectRepository,
    private val teamRepository: TeamRepository
) {

    /**
     * Determines if a user can update a specific project.
     *
     * @param userId The ID of the user attempting the action
     * @param projectId The ID of the project being accessed
     * @return true if the user is authorized, false otherwise
     */
    suspend fun canUpdateProject(userId: UserId, projectId: String): Boolean {
        // Check if user is a project owner (domain-specific rule)
        val isProjectOwner = checkProjectOwnership(userId, projectId)

        // Check if user is a team member with update permission
        val isTeamMember = checkProjectTeamMembership(userId, projectId)

        // Check if user has admin permission (from RBAC)
        val hasAdminPermission = rbacService.hasPermission(
            userId = userId,
            permission = Permission.Project.Update
        )

        // Project ownership, team membership, or admin permission grants access
        return isProjectOwner || isTeamMember || hasAdminPermission
    }

    /**
     * Determines if a user can view sensitive data within the application.
     *
     * @param userId The ID of the user attempting the action
     * @param dataType The type of sensitive data being accessed
     * @return true if the user is authorized, false otherwise
     */
    suspend fun canViewSensitiveData(userId: UserId, dataType: SensitiveDataType): Boolean {
        return when (dataType) {
            SensitiveDataType.CONFIDENTIAL ->
                rbacService.hasPermission(userId, Permission.Data.ViewConfidential)

            SensitiveDataType.RESTRICTED ->
                rbacService.hasPermission(userId, Permission.Data.ViewRestricted)

            SensitiveDataType.PUBLIC -> true // Public data is accessible to all authenticated users
        }
    }

    /**
     * Determines if a user can manage a specific team.
     *
     * @param userId The ID of the user attempting the action
     * @param teamId The ID of the team being managed
     * @return true if the user is authorized, false otherwise
     */
    suspend fun canManageTeam(userId: UserId, teamId: String): Boolean {
        // Check if user is a team mentor/instructor
        val isTeamMentor = checkTeamMentorship(userId, teamId)

        // Check if user has team management permission
        val hasTeamManagementPermission = rbacService.hasPermission(
            userId = userId,
            permission = Permission.Cohort.Team.Manage
        )

        return isTeamMentor || hasTeamManagementPermission
    }

    /**
     * Checks if a user is the owner of a specific project.
     */
    private suspend fun checkProjectOwnership(userId: UserId, projectId: String): Boolean {
        return projectRepository.findById(projectId)?.let {
            it.ownerId == userId
        } ?: false
    }

    /**
     * Checks if a user is a member of a project team.
     */
    private suspend fun checkProjectTeamMembership(userId: UserId, projectId: String): Boolean {
        return projectRepository.findById(projectId)?.let { project ->
            project.teamMembers.any { teamMember -> teamMember.userId == userId }
        } ?: false
    }

    /**
     * Checks if a user is a mentor/instructor for a specific team.
     */
    private suspend fun checkTeamMentorship(userId: UserId, teamId: String): Boolean {
        return teamRepository.findById(teamId)?.let { team ->
            team.members.any { member -> member.id == userId.value }
        } ?: false
    }
}

/**
 * Enum representing different sensitivity levels of data
 */
enum class SensitiveDataType {
    PUBLIC,
    RESTRICTED,
    CONFIDENTIAL
}
