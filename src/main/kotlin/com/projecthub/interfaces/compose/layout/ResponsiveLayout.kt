package com.projecthub.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class WindowSize {
    Compact,    // Phone portrait
    Medium,     // Phone landscape, tablet portrait
    Expanded    // Tablet landscape, desktop
}

@Composable
fun ProjectHubResponsiveLayout(
    windowSize: WindowSize,
    currentRoute: String,
    onRouteSelected: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDrawerOpen by remember { mutableStateOf(false) }

    when (windowSize) {
        WindowSize.Compact -> {
            SharedNavigationDrawer(
                currentRoute = currentRoute,
                onRouteSelected = onRouteSelected,
                onClose = { isDrawerOpen = false },
                modifier = modifier
            ) {
                Scaffold(
                    bottomBar = {
                        SharedNavigationBar(
                            currentRoute = currentRoute,
                            onRouteSelected = onRouteSelected
                        )
                    }
                ) { paddingValues ->
                    content(paddingValues)
                }
            }
        }
        WindowSize.Medium -> {
            Row(modifier = modifier.fillMaxSize()) {
                SharedNavigationRail(
                    currentRoute = currentRoute,
                    onRouteSelected = onRouteSelected
                )
                Scaffold { paddingValues ->
                    content(paddingValues)
                }
            }
        }
        WindowSize.Expanded -> {
            Row(modifier = modifier.fillMaxSize()) {
                SharedNavigationRail(
                    currentRoute = currentRoute,
                    onRouteSelected = onRouteSelected,
                    modifier = Modifier.width(80.dp)
                )
                Scaffold { paddingValues ->
                    content(paddingValues)
                }
            }
        }
    }
}

@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    return when {
        screenWidth < 600.dp -> WindowSize.Compact
        screenWidth < 840.dp -> WindowSize.Medium
        else -> WindowSize.Expanded
    }
}