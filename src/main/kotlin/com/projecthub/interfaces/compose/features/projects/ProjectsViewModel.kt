package com.projecthub.compose.features.projects

import com.projecthub.compose.bridge.ServiceBridge
import com.projecthub.compose.mvi.BaseMviViewModel
import kotlinx.coroutines.launch
import com.projecthub.compose.features.projects.ProjectsContract.*
import java.text.SimpleDateFormat
import java.util.*

class ProjectsViewModel : BaseMviViewModel<State, Intent, Effect>(State()) {
    
    override fun handleIntent(intent: Intent, currentState: State) {
        when (intent) {
            is Intent.LoadProjects -> loadProjects()
            is Intent.SelectProject -> handleProjectSelection(intent.projectId)
            is Intent.FilterProjects -> handleFilter(intent.filter)
            is Intent.SortProjects -> handleSort(intent.order)
            is Intent.RefreshProjects -> loadProjects(forceRefresh = true)
            is Intent.SearchProjects -> handleSearch(intent.query)
            is Intent.UpdateProjectStatus -> updateProjectStatus(intent.projectId, intent.status)
        }
    }
    
    private fun loadProjects(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true, error = null) }
                
                val bridge = ServiceBridge.getInstance()
                bridge.callService<List<Map<String, Any>>>(
                    "projectService",
                    "getProjects",
                    mapOf("forceRefresh" to forceRefresh)
                ).collect { result ->
                    result.onSuccess { projectMaps ->
                        val projects = projectMaps.map { map ->
                            Project(
                                id = map["id"].toString(),
                                name = map["name"].toString(),
                                description = map["description"].toString(),
                                status = ProjectStatus.valueOf(map["status"].toString()),
                                progress = (map["progress"] as Number).toFloat(),
                                startDate = parseDate(map["startDate"].toString()),
                                dueDate = map["dueDate"]?.toString()?.let { parseDate(it) },
                                teamSize = (map["teamSize"] as Number).toInt(),
                                leadId = map["leadId"]?.toString(),
                                leadName = map["leadName"]?.toString(),
                                tags = (map["tags"] as? List<*>)?.map { it.toString() } ?: emptyList()
                            )
                        }
                        updateState { 
                            copy(
                                projects = projects,
                                isLoading = false,
                                error = null
                            )
                        }
                    }.onFailure { error ->
                        val errorMessage = "Failed to load projects: ${error.message}"
                        updateState { 
                            copy(
                                isLoading = false,
                                error = errorMessage
                            )
                        }
                        emitEffect(Effect.ShowError(errorMessage))
                    }
                }
            } catch (e: Exception) {
                val errorMessage = "An error occurred: ${e.message}"
                updateState { 
                    copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
                emitEffect(Effect.ShowError(errorMessage))
            }
        }
    }
    
    private fun handleProjectSelection(projectId: String) {
        emitEffect(Effect.NavigateToProjectDetails(projectId))
    }
    
    private fun handleFilter(filter: ProjectFilter) {
        viewModelScope.launch {
            updateState { copy(selectedFilter = filter) }
            
            val filteredProjects = state.projects.filter { project ->
                when (filter) {
                    ProjectFilter.ALL -> true
                    ProjectFilter.ACTIVE -> project.status == ProjectStatus.IN_PROGRESS
                    ProjectFilter.COMPLETED -> project.status == ProjectStatus.COMPLETED
                    ProjectFilter.OVERDUE -> {
                        project.dueDate?.let { dueDate ->
                            project.status != ProjectStatus.COMPLETED && 
                            dueDate.before(Date())
                        } ?: false
                    }
                    ProjectFilter.MY_PROJECTS -> {
                        // TODO: Add actual user ID comparison
                        project.leadId != null
                    }
                }
            }
            
            updateState { copy(projects = filteredProjects) }
        }
    }
    
    private fun handleSort(order: ProjectSortOrder) {
        viewModelScope.launch {
            updateState { copy(sortOrder = order) }
            
            val sortedProjects = state.projects.sortedWith { a, b ->
                when (order) {
                    ProjectSortOrder.NEWEST_FIRST -> b.startDate.compareTo(a.startDate)
                    ProjectSortOrder.OLDEST_FIRST -> a.startDate.compareTo(b.startDate)
                    ProjectSortOrder.NAME_ASC -> a.name.compareTo(b.name)
                    ProjectSortOrder.NAME_DESC -> b.name.compareTo(a.name)
                    ProjectSortOrder.PROGRESS_ASC -> a.progress.compareTo(b.progress)
                    ProjectSortOrder.PROGRESS_DESC -> b.progress.compareTo(a.progress)
                    ProjectSortOrder.DUE_DATE_ASC -> {
                        when {
                            a.dueDate == null && b.dueDate == null -> 0
                            a.dueDate == null -> 1
                            b.dueDate == null -> -1
                            else -> a.dueDate.compareTo(b.dueDate)
                        }
                    }
                    ProjectSortOrder.DUE_DATE_DESC -> {
                        when {
                            a.dueDate == null && b.dueDate == null -> 0
                            a.dueDate == null -> 1
                            b.dueDate == null -> -1
                            else -> b.dueDate.compareTo(a.dueDate)
                        }
                    }
                }
            }
            
            updateState { copy(projects = sortedProjects) }
        }
    }
    
    private fun handleSearch(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadProjects()
                return@launch
            }
            
            try {
                updateState { copy(isLoading = true, error = null) }
                
                val bridge = ServiceBridge.getInstance()
                bridge.callService<List<Map<String, Any>>>(
                    "projectService",
                    "searchProjects",
                    mapOf("query" to query)
                ).collect { result ->
                    result.onSuccess { projectMaps ->
                        val projects = projectMaps.map { map ->
                            Project(
                                id = map["id"].toString(),
                                name = map["name"].toString(),
                                description = map["description"].toString(),
                                status = ProjectStatus.valueOf(map["status"].toString()),
                                progress = (map["progress"] as Number).toFloat(),
                                startDate = parseDate(map["startDate"].toString()),
                                dueDate = map["dueDate"]?.toString()?.let { parseDate(it) },
                                teamSize = (map["teamSize"] as Number).toInt(),
                                leadId = map["leadId"]?.toString(),
                                leadName = map["leadName"]?.toString(),
                                tags = (map["tags"] as? List<*>)?.map { it.toString() } ?: emptyList()
                            )
                        }
                        updateState { 
                            copy(
                                projects = projects,
                                isLoading = false,
                                error = null
                            )
                        }
                    }.onFailure { error ->
                        val errorMessage = "Failed to search projects: ${error.message}"
                        updateState { 
                            copy(
                                isLoading = false,
                                error = errorMessage
                            )
                        }
                        emitEffect(Effect.ShowError(errorMessage))
                    }
                }
            } catch (e: Exception) {
                val errorMessage = "An error occurred while searching: ${e.message}"
                updateState { 
                    copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
                emitEffect(Effect.ShowError(errorMessage))
            }
        }
    }
    
    private fun updateProjectStatus(projectId: String, status: ProjectStatus) {
        viewModelScope.launch {
            try {
                val bridge = ServiceBridge.getInstance()
                bridge.callService<Unit>(
                    "projectService",
                    "updateProjectStatus",
                    mapOf(
                        "projectId" to projectId,
                        "status" to status.name
                    )
                ).collect { result ->
                    result.onSuccess {
                        // Update local state
                        updateState {
                            copy(
                                projects = projects.map { project ->
                                    if (project.id == projectId) {
                                        project.copy(status = status)
                                    } else {
                                        project
                                    }
                                }
                            )
                        }
                        emitEffect(Effect.ShowSuccess("Project status updated"))
                    }.onFailure { error ->
                        emitEffect(Effect.ShowError("Failed to update project status: ${error.message}"))
                    }
                }
            } catch (e: Exception) {
                emitEffect(Effect.ShowError("Failed to update project status: ${e.message}"))
            }
        }
    }
    
    private fun parseDate(dateStr: String): Date {
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(dateStr)
                ?: throw IllegalArgumentException("Invalid date format")
        } catch (e: Exception) {
            Date() // Fallback to current date if parsing fails
        }
    }
}