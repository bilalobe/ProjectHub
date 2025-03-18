package com.projecthub.ui.cohort.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projecthub.ui.cohort.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class CohortViewModel(
    private val cohortRepository: ICohortRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CohortState())
    val state: StateFlow<CohortState> = _state.asStateFlow()

    fun emitEvent(event: CohortEvent) {
        when (event) {
            is CohortEvent.LoadCohorts -> loadCohorts()
            is CohortEvent.SelectCohort -> selectCohort(event.cohortId)
            is CohortEvent.CreateCohort -> createCohort(event.cohort)
            is CohortEvent.UpdateCohort -> updateCohort(event.cohort)
            is CohortEvent.UpdateCohortStatus -> updateCohortStatus(event.cohortId, event.status)
            is CohortEvent.EnrollStudent -> enrollStudent(event.cohortId, event.studentId)
            is CohortEvent.UpdateEnrollmentStatus -> updateEnrollmentStatus(event.cohortId, event.studentId, event.status)
            is CohortEvent.AssignInstructor -> assignInstructor(event.cohortId, event.instructorId)
            is CohortEvent.RemoveInstructor -> removeInstructor(event.cohortId, event.instructorId)
            is CohortEvent.ClearError -> clearError()
        }
    }

    private fun loadCohorts() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val cohorts = cohortRepository.getCohorts()
                _state.value = _state.value.copy(
                    cohorts = cohorts,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to load cohorts: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun selectCohort(cohortId: String) {
        val selectedCohort = _state.value.cohorts.find { it.id == cohortId }
        _state.value = _state.value.copy(selectedCohort = selectedCohort)
    }

    private fun createCohort(cohort: Cohort) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val newCohort = cohortRepository.createCohort(cohort)
                _state.value = _state.value.copy(
                    cohorts = _state.value.cohorts + newCohort,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to create cohort: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun updateCohort(cohort: Cohort) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val updatedCohort = cohortRepository.updateCohort(cohort)
                _state.value = _state.value.copy(
                    cohorts = _state.value.cohorts.map { 
                        if (it.id == cohort.id) updatedCohort else it 
                    },
                    selectedCohort = if (_state.value.selectedCohort?.id == cohort.id) {
                        updatedCohort
                    } else {
                        _state.value.selectedCohort
                    },
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to update cohort: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun updateCohortStatus(cohortId: String, status: CohortStatus) {
        val cohort = _state.value.cohorts.find { it.id == cohortId } ?: return
        updateCohort(cohort.copy(status = status))
    }

    private fun enrollStudent(cohortId: String, studentId: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val student = StudentInfo(
                    id = studentId,
                    name = "", // To be populated from student repository
                    email = "", // To be populated from student repository
                    enrollmentDate = LocalDate.now(),
                    status = EnrollmentStatus.ENROLLED
                )
                val enrolled = cohortRepository.enrollStudent(cohortId, student)
                if (enrolled) {
                    loadCohorts() // Refresh cohort data
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to enroll student: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun updateEnrollmentStatus(cohortId: String, studentId: String, status: EnrollmentStatus) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val updated = cohortRepository.updateEnrollmentStatus(cohortId, studentId, status)
                if (updated) {
                    loadCohorts() // Refresh cohort data
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to update enrollment status: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun assignInstructor(cohortId: String, instructorId: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val instructor = InstructorInfo(
                    id = instructorId,
                    name = "", // To be populated from instructor repository
                    email = "", // To be populated from instructor repository
                    specialization = "", // To be populated from instructor repository
                    assignedDate = LocalDate.now()
                )
                val assigned = cohortRepository.assignInstructor(cohortId, instructor)
                if (assigned) {
                    loadCohorts() // Refresh cohort data
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to assign instructor: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun removeInstructor(cohortId: String, instructorId: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val removed = cohortRepository.removeInstructor(cohortId, instructorId)
                if (removed) {
                    loadCohorts() // Refresh cohort data
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to remove instructor: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

interface ICohortRepository {
    suspend fun getCohorts(): List<Cohort>
    suspend fun createCohort(cohort: Cohort): Cohort
    suspend fun updateCohort(cohort: Cohort): Cohort
    suspend fun enrollStudent(cohortId: String, student: StudentInfo): Boolean
    suspend fun updateEnrollmentStatus(cohortId: String, studentId: String, status: EnrollmentStatus): Boolean
    suspend fun assignInstructor(cohortId: String, instructor: InstructorInfo): Boolean
    suspend fun removeInstructor(cohortId: String, instructorId: String): Boolean
}