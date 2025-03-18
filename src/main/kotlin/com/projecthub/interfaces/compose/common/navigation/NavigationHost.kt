package com.projecthub.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.projecthub.compose.features.users.UsersScreen
import com.projecthub.compose.features.users.UsersViewModel
import com.projecthub.compose.features.projects.ProjectsScreen
import com.projecthub.compose.features.projects.ProjectsViewModel
import com.projecthub.compose.features.teams.TeamsScreen
import com.projecthub.compose.features.teams.TeamsViewModel
import com.projecthub.compose.features.milestone.MilestonesScreen
import com.projecthub.compose.features.milestone.MilestoneViewModel

@Composable
fun NavigationHost(
    navController: NavHostController,
    startDestination: String = Destination.Users.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Users feature
        composable(Destination.Users.route) {
            UsersScreen(
                viewModel = UsersViewModel(),
                onNavigateToUserDetails = { userId ->
                    navController.navigate(Destination.UserDetails(userId).route)
                }
            )
        }
        composable(
            route = Destination.USER_DETAILS_ROUTE,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
                ?: return@composable
            // TODO: Implement UserDetailsScreen
        }

        // Projects feature
        composable(Destination.Projects.route) {
            ProjectsScreen(
                viewModel = ProjectsViewModel(),
                onNavigateToProjectDetails = { projectId ->
                    navController.navigate(Destination.ProjectDetails(projectId).route)
                }
            )
        }
        composable(
            route = Destination.PROJECT_DETAILS_ROUTE,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
                ?: return@composable
            // TODO: Implement ProjectDetailsScreen
        }

        // Teams feature
        composable(Destination.Teams.route) {
            TeamsScreen(
                viewModel = TeamsViewModel(),
                onNavigateToTeamDetails = { teamId ->
                    navController.navigate(Destination.TeamDetails(teamId).route)
                }
            )
        }
        composable(
            route = Destination.TEAM_DETAILS_ROUTE,
            arguments = listOf(
                navArgument("teamId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getString("teamId")
                ?: return@composable
            // TODO: Implement TeamDetailsScreen
        }

        // Milestones feature
        composable(
            route = Destination.PROJECT_MILESTONES_ROUTE,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
                ?: return@composable
            
            MilestonesScreen(
                viewModel = MilestoneViewModel(projectId),
                onNavigateToMilestoneDetails = { milestoneId ->
                    navController.navigate(
                        Destination.MilestoneDetails(projectId, milestoneId).route
                    )
                }
            )
        }

        composable(
            route = Destination.MILESTONE_DETAILS_ROUTE,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType },
                navArgument("milestoneId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
                ?: return@composable
            val milestoneId = backStackEntry.arguments?.getString("milestoneId")
                ?: return@composable

            MilestonesScreen(
                viewModel = MilestoneViewModel(projectId),
                selectedMilestoneId = milestoneId
            )
        }
    }
}