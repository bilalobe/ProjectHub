package com.projecthub.ui.layout

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ThemeAwareSidebar(
    isExpanded: Boolean,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Surface(
        color = colorScheme.surfaceVariant,
        tonalElevation = 1.dp,
        modifier = modifier
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            Column(
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight()
                    .padding(8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SidebarSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun SidebarItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: @Composable (() -> Unit)? = null
) {
    Surface(
        selected = selected,
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = if (selected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            badge?.invoke()
        }
    }
}

@Composable
fun SidebarBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}