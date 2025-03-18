package com.projecthub.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.projecthub.compose.navigation.Destination
import com.projecthub.compose.navigation.NavigationHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectHubApp() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                val items = listOf(
                    NavigationItem(
                        route = Destination.Users.route,
                        icon = Icons.Default.Person,
                        label = "Users"
                    ),
                    NavigationItem(
                        route = Destination.Projects.route,
                        icon = Icons.Default.Work,
                        label = "Projects"
                    ),
                    NavigationItem(
                        route = Destination.Teams.route,
                        icon = Icons.Default.Group,
                        label = "Teams"
                    )
                )
                
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NavigationHost(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private data class NavigationItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)