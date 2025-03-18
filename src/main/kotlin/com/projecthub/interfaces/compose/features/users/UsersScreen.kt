package com.projecthub.compose.features.users

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.projecthub.compose.components.SearchBar
import com.projecthub.compose.features.users.UsersContract.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    viewModel: UsersViewModel,
    onNavigateToUserDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.stateFlow.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Collect side effects
    LaunchedEffect(viewModel) {
        viewModel.collectEffects { effect ->
            when (effect) {
                is Effect.NavigateToUserDetails -> {
                    onNavigateToUserDetails(effect.userId)
                }
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
    
    // Load initial data
    LaunchedEffect(Unit) {
        viewModel.processIntent(Intent.LoadUsers)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Users") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    // Filter menu
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.FilterList, "Filter Users")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        UserFilter.values().forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter.name.replace("_", " ").lowercase().capitalize()) },
                                onClick = {
                                    viewModel.processIntent(Intent.FilterUsers(filter))
                                    expanded = false
                                },
                                leadingIcon = {
                                    if (state.selectedFilter == filter) {
                                        Icon(Icons.Default.Check, null)
                                    }
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add new user intent */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.PersonAdd, "Add User")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search bar
                SearchBar(
                    onSearch = { query ->
                        viewModel.processIntent(Intent.SearchUsers(query))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                
                // Users grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.users,
                        key = { it.id }
                    ) { user ->
                        UserCard(
                            user = user,
                            onClick = {
                                viewModel.processIntent(Intent.SelectUser(user.id))
                            }
                        )
                    }
                }
                
                // Empty state
                if (state.users.isEmpty() && !state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (state.error != null) 
                                "An error occurred. Pull to refresh." 
                            else 
                                "No users found",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            
            // Loading indicator
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserCard(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.8f),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // User Avatar
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = user.role,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AssignmentChip(user.activeAssignments)
        }
    }
}

@Composable
private fun AssignmentChip(count: Int) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "$count active ${if (count == 1) "project" else "projects"}",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}