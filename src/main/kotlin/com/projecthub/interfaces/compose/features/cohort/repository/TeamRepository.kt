package com.projecthub.ui.cohort.repository

import com.projecthub.base.team.domain.entity.Team
import java.util.UUID

/**
 * Repository interface for accessing and managing team data.
 * Platform-specific implementations will be provided for each target platform.
 */
interface TeamRepository {
    /**
     * Get a team by its ID.
     *
     * @param id The team ID
     * @return The team entity
     */
    suspend fun getTeamById(id: UUID): Team

    /**
     * Get all teams for a cohort.
     *
     * @param cohortId The cohort ID
     * @return List of team entities
     */
    suspend fun getTeamsByCohortId(cohortId: UUID): List<Team>

    /**
     * Create a new team.
     *
     * @param team The team to create
     * @return The created team
     */
    suspend fun createTeam(team: Team): Team

    /**
     * Update an existing team.
     *
     * @param id The team ID
     * @param team The updated team data
     * @return The updated team
     */
    suspend fun updateTeam(id: UUID, team: Team): Team

    /**
     * Delete a team.
     *
     * @param id The team ID
     */
    suspend fun deleteTeam(id: UUID)

    /**
     * Update the position of a team in the seating arrangement.
     *
     * @param teamId The team ID
     * @param row The row position
     * @param column The column position
     * @return The updated team
     */
    suspend fun updateTeamPosition(teamId: UUID, row: Int, column: Int): Team

    /**
     * Clear the position of a team, removing it from the seating arrangement.
     *
     * @param teamId The team ID
     * @return The updated team
     */
    suspend fun clearTeamPosition(teamId: UUID): Team

    /**
     * Get all teams with their positions for a cohort.
     *
     * @param cohortId The cohort ID
     * @return Map of team IDs to their positions
     */
    suspend fun getTeamPositions(cohortId: UUID): Map<UUID, Pair<Int, Int>>

    /**
     * Update a team's confidence score, which can be used for confidence-based seating.
     *
     * @param teamId The team ID
     * @param confidenceScore The new confidence score
     * @return The updated team
     */
    suspend fun updateTeamConfidenceScore(teamId: UUID, confidenceScore: Int): Team
}