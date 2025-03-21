package com.projecthub.interfaces.compose.security

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.projecthub.interfaces.compose.security.UiPermissionController

/**
 * Composition local to provide the UiPermissionController throughout the UI hierarchy
 */
val LocalPermissionController = compositionLocalOf<UiPermissionController?> { null }

/**
 * Provider component that makes the UiPermissionController available to all child components
 */
@Composable
fun PermissionProvider(
    permissionController: UiPermissionController,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalPermissionController provides permissionController
    ) {
        content()
    }
}

/**
 * Composable that conditionally renders content based on a permission check
 */
@Composable
fun PermissionRequired(
    objectName: String,
    operation: String,
    objectId: String? = null,
    content: @Composable () -> Unit,
    fallback: @Composable () -> Unit = {}
) {
    val permissionController = LocalPermissionController.current
        ?: throw IllegalStateException("PermissionRequired must be used within a PermissionProvider")
    
    val session by permissionController.observeSession().collectAsState(initial = null)
    val hasPermission = remember(session, objectName, operation, objectId) {
        permissionController.hasPermission(objectName, operation, objectId)
    }
    
    if (hasPermission) {
        content()
    } else {
        fallback()
    }
}

/**
 * Composable that conditionally renders content based on a role check
 */
@Composable
fun RoleRequired(
    roleName: String,
    content: @Composable () -> Unit,
    fallback: @Composable () -> Unit = {}
) {
    val permissionController = LocalPermissionController.current
        ?: throw IllegalStateException("RoleRequired must be used within a PermissionProvider")
    
    val session by permissionController.observeSession().collectAsState(initial = null)
    val hasRole = remember(session, roleName) {
        permissionController.hasRole(roleName)
    }
    
    if (hasRole) {
        content()
    } else {
        fallback()
    }
}

/**
 * Composable that conditionally applies multiple permission checks
 */
@Composable
fun MultiPermissionRequired(
    permissions: List<Triple<String, String, String?>>,
    requireAll: Boolean = true,
    content: @Composable () -> Unit,
    fallback: @Composable () -> Unit = {}
) {
    val permissionController = LocalPermissionController.current
        ?: throw IllegalStateException("MultiPermissionRequired must be used within a PermissionProvider")
    
    val session by permissionController.observeSession().collectAsState(initial = null)
    
    val hasPermission = remember(session, permissions) {
        if (requireAll) {
            permissions.all { (objectName, operation, objectId) ->
                permissionController.hasPermission(objectName, operation, objectId)
            }
        } else {
            permissions.any { (objectName, operation, objectId) ->
                permissionController.hasPermission(objectName, operation, objectId)
            }
        }
    }
    
    if (hasPermission) {
        content()
    } else {
        fallback()
    }
}