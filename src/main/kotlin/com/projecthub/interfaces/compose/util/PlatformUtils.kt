package com.projecthub.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Platform types that our application supports
 */
enum class Platform {
    DESKTOP,
    ANDROID,
    UNKNOWN
}

/**
 * Composition local for current platform information
 */
val LocalPlatform = staticCompositionLocalOf<Platform> { Platform.UNKNOWN }

/**
 * Utility function to determine if the current platform is a desktop
 */
@Composable
fun isDesktop(): Boolean = LocalPlatform.current == Platform.DESKTOP

/**
 * Utility function to determine if the current platform is mobile (Android)
 */
@Composable
fun isMobile(): Boolean = LocalPlatform.current == Platform.ANDROID

/**
 * Execute different code blocks based on the current platform
 */
@Composable
inline fun <T> platformSpecific(
    desktop: @Composable () -> T,
    mobile: @Composable () -> T
): T {
    return when (LocalPlatform.current) {
        Platform.DESKTOP -> desktop()
        Platform.ANDROID -> mobile()
        else -> desktop() // Default to desktop if unknown
    }
}

/**
 * Returns an appropriate padding for different platforms
 */
@Composable
fun platformPadding(): Dp {
    return when (LocalPlatform.current) {
        Platform.DESKTOP -> 16.dp
        Platform.ANDROID -> 8.dp
        else -> 12.dp
    }
}

/**
 * Returns an appropriate item size for different platforms
 */
@Composable
fun platformItemSize(desktopSize: Dp, mobileSize: Dp): Dp {
    return when (LocalPlatform.current) {
        Platform.DESKTOP -> desktopSize
        Platform.ANDROID -> mobileSize
        else -> desktopSize
    }
}