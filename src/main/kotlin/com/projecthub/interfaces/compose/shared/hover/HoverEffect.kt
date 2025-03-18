package com.projecthub.ui.shared.hover

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Utility for adding hover effects to Compose UI elements on Desktop and Web platforms.
 * This allows for responsive hover interactions that enhance the user experience.
 */
object HoverEffect {
    /**
     * Creates a modifier with hover detection.
     *
     * @param onHover Lambda that receives the hover state (true when hovered)
     * @return Modifier with hover detection
     */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Modifier.withHoverState(onHover: (Boolean) -> Unit): Modifier {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()

        onHover(isHovered)
        
        return this.hoverable(interactionSource)
    }

    /**
     * Creates a modifier with hover detection that returns the combined modifier.
     * Useful for applying conditional styling based on hover state.
     *
     * @param onHovered Modifier to apply when the element is hovered
     * @param onNotHovered Modifier to apply when the element is not hovered
     * @return Combined modifier with hover detection and conditional styling
     */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Modifier.withConditionalHover(
        onHovered: Modifier.() -> Modifier,
        onNotHovered: Modifier.() -> Modifier = { this }
    ): Modifier {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()
        
        return this
            .hoverable(interactionSource)
            .then(if (isHovered) onHovered(Modifier) else onNotHovered(Modifier))
    }

    /**
     * Simple extension to check if the element is hovered.
     * 
     * @return State indicating whether the element is currently hovered
     */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Modifier.isHovered(): Pair<Modifier, Boolean> {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()
        
        return Pair(this.hoverable(interactionSource), isHovered)
    }
}