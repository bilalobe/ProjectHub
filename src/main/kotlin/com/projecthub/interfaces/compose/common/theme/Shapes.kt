package com.projecthub.compose.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape definitions for ProjectHub that work across both desktop and mobile platforms.
 */
val ProjectHubShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp)
)