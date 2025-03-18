package com.projecthub.domain.cohort

import com.projecthub.domain.ValueObject
import com.projecthub.domain.team.TeamPosition
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import jakarta.persistence.JoinColumn

/**
 * Configuration for team seating arrangements within a cohort.
 * This defines the physical or virtual layout where teams are positioned.
 */
@Embeddable
data class SeatingConfiguration(
    @Column(name = "seating_rows", nullable = false)
    val rows: Int,

    @Column(name = "seating_columns", nullable = false)
    val columns: Int,

    @ElementCollection
    @CollectionTable(
        name = "cohort_blocked_positions",
        joinColumns = [JoinColumn(name = "cohort_id")]
    )
    val blockedPositions: Set<BlockedPosition> = emptySet()
) : ValueObject {

    init {
        require(rows > 0) { "Rows must be positive" }
        require(columns > 0) { "Columns must be positive" }
    }

    /**
     * Check if a position is valid within this seating configuration
     */
    fun isValidPosition(position: TeamPosition): Boolean {
        // Check boundaries
        if (position.row < 0 || position.row >= rows) return false
        if (position.column < 0 || position.column >= columns) return false

        // Check if position is blocked
        return !blockedPositions.any {
            it.row == position.row && it.column == position.column
        }
    }

    /**
     * Calculate the total capacity of the seating arrangement
     */
    fun totalCapacity(): Int = (rows * columns) - blockedPositions.size

    companion object {
        /**
         * Create a grid seating arrangement with no blocked positions
         */
        fun createGrid(rows: Int, columns: Int): SeatingConfiguration {
            return SeatingConfiguration(rows, columns)
        }
    }
}

/**
 * Represents positions in the seating arrangement that cannot be occupied by teams
 */
@Embeddable
data class BlockedPosition(
    @Column(name = "blocked_row", nullable = false)
    val row: Int,

    @Column(name = "blocked_column", nullable = false)
    val column: Int
) : ValueObject
