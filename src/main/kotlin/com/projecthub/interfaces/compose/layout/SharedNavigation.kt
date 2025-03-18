package com.projecthub.ui.layout

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun SharedNavigationRail(
    currentRoute: String,
    onRouteSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(modifier = modifier) {
        NavigationRailItem(
            selected = currentRoute == "dashboard",
            onClick = { onRouteSelected("dashboard") },
            icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
            label = { Text("Dashboard") }
        )
        NavigationRailItem(
            selected = currentRoute == "students",
            onClick = { onRouteSelected("students") },
            icon = { Icon(Icons.Default.School, "Students") },
            label = { Text("Students") }
        )
        NavigationRailItem(
            selected = currentRoute == "cohorts",
            onClick = { onRouteSelected("cohorts") },
            icon = { Icon(Icons.Default.Groups, "Cohorts") },
            label = { Text("Cohorts") }
        )
        NavigationRailItem(
            selected = currentRoute == "submissions",
            onClick = { onRouteSelected("submissions") },
            icon = { Icon(Icons.Default.Assignment, "Submissions") },
            label = { Text("Submissions") }
        )
        NavigationRailItem(
            selected = currentRoute == "settings",
            onClick = { onRouteSelected("settings") },
            icon = { Icon(Icons.Default.Settings, "Settings") },
            label = { Text("Settings") }
        )
    }
}

@Composable
fun SharedNavigationBar(
    currentRoute: String,
    onRouteSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = currentRoute == "dashboard",
            onClick = { onRouteSelected("dashboard") },
            icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
            label = { Text("Dashboard") }
        )
        NavigationBarItem(
            selected = currentRoute == "students",
            onClick = { onRouteSelected("students") },
            icon = { Icon(Icons.Default.School, "Students") },
            label = { Text("Students") }
        )
        NavigationBarItem(
            selected = currentRoute == "cohorts",
            onClick = { onRouteSelected("cohorts") },
            icon = { Icon(Icons.Default.Groups, "Cohorts") },
            label = { Text("Cohorts") }
        )
        NavigationBarItem(
            selected = currentRoute == "submissions",
            onClick = { onRouteSelected("submissions") },
            icon = { Icon(Icons.Default.Assignment, "Submissions") },
            label = { Text("Submissions") }
        )
    }
}

@Composable
fun SharedNavigationDrawer(
    currentRoute: String,
    onRouteSelected: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    selected = currentRoute == "dashboard",
                    onClick = { 
                        onRouteSelected("dashboard")
                        onClose()
                    },
                    icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
                    label = { Text("Dashboard") }
                )
                NavigationDrawerItem(
                    selected = currentRoute == "students",
                    onClick = { 
                        onRouteSelected("students")
                        onClose()
                    },
                    icon = { Icon(Icons.Default.School, "Students") },
                    label = { Text("Students") }
                )
                NavigationDrawerItem(
                    selected = currentRoute == "cohorts",
                    onClick = { 
                        onRouteSelected("cohorts")
                        onClose()
                    },
                    icon = { Icon(Icons.Default.Groups, "Cohorts") },
                    label = { Text("Cohorts") }
                )
                NavigationDrawerItem(
                    selected = currentRoute == "submissions",
                    onClick = { 
                        onRouteSelected("submissions")
                        onClose()
                    },
                    icon = { Icon(Icons.Default.Assignment, "Submissions") },
                    label = { Text("Submissions") }
                )
                NavigationDrawerItem(
                    selected = currentRoute == "settings",
                    onClick = { 
                        onRouteSelected("settings")
                        onClose()
                    },
                    icon = { Icon(Icons.Default.Settings, "Settings") },
                    label = { Text("Settings") }
                )
            }
        },
        modifier = modifier
    )
}