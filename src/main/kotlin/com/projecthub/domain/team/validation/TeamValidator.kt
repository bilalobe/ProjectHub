package com.projecthub.domain.team.validation

import com.projecthub.domain.validation.ValidationResult
import com.projecthub.domain.validation.Validator
import com.projecthub.domain.team.Team
import com.projecthub.domain.team.TeamPosition
import com.projecthub.domain.cohort.SeatingConfiguration
import org.springframework.stereotype.Component

@Component
class TeamValidator : Validator<Team> {

    fun validateTeamPosition(
        team: Team,
        position: TeamPosition,
        seatingConfig: SeatingConfiguration?
    ): ValidationResult {
        return ValidationResult.Builder().apply {
            // If no seating config is defined, position is always valid
            if (seatingConfig == null) return@apply

            // Check row boundaries
            if (position.row < 0 || position.row >= seatingConfig.rows) {
                addError("Row position must be between 0 and ${seatingConfig.rows - 1}")
            }

            // Check column boundaries
            if (position.column < 0 || position.column >= seatingConfig.columns) {
                addError("Column position must be between 0 and ${seatingConfig.columns - 1}")
            }

            // Check if position is already occupied by another team
            val isPositionTaken = team.cohort.teams
                .filter { it.id != team.id }
                .any { otherTeam ->
                    otherTeam.position?.let { pos ->
                        pos.row == position.row && pos.column == position.column
                    } ?: false
                }

            if (isPositionTaken) {
                addError("Position (${position.row}, ${position.column}) is already occupied by another team")
            }

            // Check if position is in a blocked area of the seating configuration
            if (seatingConfig.blockedPositions.any { blocked ->
                    blocked.row == position.row && blocked.column == position.column
                }) {
                addError("Position (${position.row}, ${position.column}) is in a blocked area")
            }
        }.build()
    }

    fun validateCreate(team: Team): ValidationResult {
        return ValidationResult.Builder().apply {
            validateBasicFields(team)
            validateTeamComposition(team)
            validateUniqueName(team)
        }.build()
    }

    private fun ValidationResult.Builder.validateBasicFields(team: Team) {
        if (team.name.isBlank()) {
            addError("Team name is mandatory")
        }
        if (team.name.length > 100) {
            addError("Team name must be less than 100 characters")
        }
    }

    private fun ValidationResult.Builder.validateTeamComposition(team: Team) {
        if (team.students.isEmpty()) {
            addError("Team must have at least one student")
        }
        if (team.students.size > Team.MAX_STUDENTS) {
            addError("Team cannot have more than ${Team.MAX_STUDENTS} students")
        }
        if (team.members.isEmpty()) {
            addError("Team must have at least one member (mentor/instructor)")
        }
        if (team.members.size > Team.MAX_MEMBERS) {
            addError("Team cannot have more than ${Team.MAX_MEMBERS} members")
        }
    }

    private fun ValidationResult.Builder.validateUniqueName(team: Team) {
        val isNameTaken = team.cohort.teams
            .filter { it.id != team.id }
            .any { it.name.equals(team.name, ignoreCase = true) }

        if (isNameTaken) {
            addError("Team name '${team.name}' is already in use within this school")
        }
    }
}
