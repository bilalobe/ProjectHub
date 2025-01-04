package com.projecthub.mobile.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projecthub.mobile.ui.projects.ProjectsScreen
import com.projecthub.mobile.ui.teams.TeamsScreen
import com.projecthub.mobile.ui.users.UsersScreen

@Composable
fun ProjectHubNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "projects") {
        composable("projects") { ProjectsScreen() }
        composable("teams") { TeamsScreen() }
        composable("users") { UsersScreen() }
    }
}
