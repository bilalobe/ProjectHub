package com.projecthub.compose.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A card component that displays a statistic with an optional icon.
 * This can be reused across both desktop and mobile UIs.
 * 
 * @param title The title of the statistic
 * @param value The value to display
 * @param icon Optional icon to display
 * @param iconTint The color of the icon
 * @param modifier Additional modifiers
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colors.primary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .heightIn(min = 120.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Text(
                text = value,
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
        }
    }
}