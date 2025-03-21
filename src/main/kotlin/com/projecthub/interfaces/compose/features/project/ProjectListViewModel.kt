package com.projecthub.interfaces.compose.features.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.projecthub.interfaces.compose.architecture.SecureMviViewModel
import com.projecthub.interfaces.compose.architecture.UiEvent
import com.projecthub.interfaces.compose.architecture.UiIntent
import com.projecthub.interfaces.compose.architecture.UiState
import com.projecthub.interfaces.compose.data.Repository
import com.projecthub.interfaces.compose.data.security.SecureEntity
import com.projecthub.interfaces.compose.security.PermissionProvider
import com.projecthub.interfaces.compose.security.PermissionRequired
import com.projecthub.interfaces.compose.security.UiPermissionController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import com.projecthub.compose.features.projects.ProjectsContract.*

/**
 * Data class representing a project entity in the UI
 */
data class ProjectUiModel(
    val id: UUID,
    val name: String,
    val description: String,
    val status: String
) : SecureEntity {
    override fun getPermissionObjectName(): String = "project"
    override fun getPermissionObjectId(): String = id.toString()
}

/**
 * Project list screen state
 */
data class ProjectListState(
    val projects: List<ProjectUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState

/**
 * Project list screen events
 */
sealed class ProjectListEvent : UiEvent {
    data class NavigateToProjectDetail(val projectId: UUID) : ProjectListEvent()
    data class ShowError(val message: String) : ProjectListEvent()
    object NavigateToCreateProject : ProjectListEvent()
}

/**
 * Project list screen intents
 */
sealed class ProjectListIntent : UiIntent {
    object LoadProjects : ProjectListIntent()
    data class ProjectClicked(val projectId: UUID) : ProjectListIntent()
    object CreateProjectClicked : ProjectListIntent()
    data class DeleteProjectClicked(val projectId: UUID) : ProjectListIntent()
}

/**
 * Project list ViewModel using our SecureMviViewModel base class
 */
class ProjectListViewModel(
    private val projectRepository: Repository<ProjectUiModel, UUID>,
    permissionController: UiPermissionController
) : SecureMviViewModel<ProjectListState, ProjectListIntent, Effect>(
    initialState = ProjectListState(isLoading = true),
    permissionController = permissionController
) {
    
    init {
        // Load projects when ViewModel is created
        processIntent(ProjectListIntent.LoadProjects)
    }
    
    override fun processIntent(intent: ProjectListIntent) {
        when (intent) {
            is ProjectListIntent.LoadProjects -> loadProjects()
            is ProjectListIntent.ProjectClicked -> emitEffect(Effect.NavigateToProjectDetails(intent.projectId.toString()))
            is ProjectListIntent.CreateProjectClicked -> {
                // Check if user has create permission before navigating
                if (hasPermission("project", "create")) {
                    emitEffect(Effect.ShowSuccess("Navigating to create project"))
                } else {
                    emitEffect(Effect.ShowError("You don't have permission to create projects"))
                }
            }
            is ProjectListIntent.DeleteProjectClicked -> deleteProject(intent.projectId)
        }
    }
    
    private fun loadProjects() {
        updateState { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                // Collect projects from the repository
                // The repository automatically filters by permission
                projectRepository.getAll().collectLatest { projects ->
                    updateState { it.copy(projects = projects, isLoading = false) }
                }
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false, error = e.message) }
                emitEffect(Effect.ShowError("Failed to load projects: ${e.message}"))
            }
        }
    }
    
    private fun deleteProject(projectId: UUID) {
        // Check if user has delete permission for this specific project
        if (!hasPermission("project", "delete", projectId.toString())) {
            emitEffect(Effect.ShowError("You don't have permission to delete this project"))
            return
        }
        
        viewModelScope.launch {
            try {
                projectRepository.deleteById(projectId)
                // Reload projects after deletion
                loadProjects()
            } catch (e: Exception) {
                emitEffect(Effect.ShowError("Failed to delete project: ${e.message}"))
            }
        }
    }
}