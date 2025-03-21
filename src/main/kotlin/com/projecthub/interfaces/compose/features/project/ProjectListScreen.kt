package com.projecthub.interfaces.compose.features.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.projecthub.interfaces.compose.security.PermissionProvider
import com.projecthub.interfaces.compose.security.PermissionRequired
import com.projecthub.interfaces.compose.security.UiPermissionController
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Project list screen Compose UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    viewModel: ProjectListViewModel,
    permissionController: UiPermissionController,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect side effects
    LaunchedEffect(viewModel) {
        viewModel.collectEffects { effect ->
            when (effect) {
                is Effect.NavigateToProjectDetails -> onNavigateToDetail(effect.projectId)
                is Effect.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Long
                    )
                }
                is Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Projects") },
                actions = {
                    PermissionRequired(
                        objectName = "project",
                        operation = "create"
                    ) {
                        IconButton(onClick = { viewModel.processIntent(ProjectListIntent.CreateProjectClicked) }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Create Project")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.projects.isEmpty()) {
                Text(
                    text = "No projects found",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.projects) { project ->
                        ProjectListItem(
                            project = project,
                            onClick = { viewModel.processIntent(ProjectListIntent.ProjectClicked(project.id)) },
                            onDeleteClick = { viewModel.processIntent(ProjectListIntent.DeleteProjectClicked(project.id)) }
                        )
                    }
                }
            }

            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * Project list item component with permission-based rendering
 */
@Composable
fun ProjectListItem(
    project: ProjectUiModel,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                
                // Only show delete button if user has permission for this specific project
                PermissionRequired(
                    objectName = "project",
                    operation = "delete",
                    objectId = project.id.toString()
                ) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Project"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status: ${project.status}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}