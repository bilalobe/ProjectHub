package com.projecthub.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.projecthub.base.auth.api.dto.AuthResponseDTO
import com.projecthub.ui.shared.components.UserAvatar

@Composable
fun SharedHeader(
    currentUser: AuthResponseDTO?,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ProjectHub")
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, "Menu")
            }
        },
        actions = {
            if (currentUser != null) {
                var showMenu by remember { mutableStateOf(false) }
                
                IconButton(onClick = { showMenu = true }) {
                    UserAvatar(
                        user = currentUser.user,
                        size = 32.dp
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Profile") },
                        leadingIcon = { Icon(Icons.Default.Person, "Profile") },
                        onClick = {
                            showMenu = false
                            onProfileClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        leadingIcon = { Icon(Icons.Default.ExitToApp, "Logout") },
                        onClick = {
                            showMenu = false
                            onLogoutClick()
                        }
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun SharedHeaderTitle(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = if (onBackClick != null) {
            {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            }
        } else null,
        actions = actions ?: {},
        modifier = modifier
    )
}