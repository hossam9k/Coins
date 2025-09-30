package org.poc.app.shared.design.theme.core

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Platform-specific theming support
 * Provides platform-optimized color schemes and behaviors
 */

/**
 * Platform theme configuration
 */
data class PlatformThemeConfig(
    val supportsDynamicColors: Boolean,
    val supportsHighRefreshRate: Boolean,
    val hasNotch: Boolean,
    val preferredNavigationStyle: NavigationStyle,
    val systemAccentColor: Color?
)

enum class NavigationStyle {
    SYSTEM_BARS,     // Android system bars
    HOME_INDICATOR,  // iOS home indicator
    HYBRID          // Both systems
}

/**
 * Get platform-specific theme configuration
 */
@Composable
expect fun getPlatformThemeConfig(): PlatformThemeConfig

/**
 * Get platform-optimized color scheme
 */
@Composable
expect fun getPlatformColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean
): ColorScheme

/**
 * Platform-specific theme adjustments
 */
@Composable
expect fun PlatformThemeAdjustments(content: @Composable () -> Unit)

/**
 * Get safe area insets for platform
 */
@Composable
expect fun getSafeAreaInsets(): SafeAreaInsets

/**
 * Platform-specific edge-to-edge configuration
 */
@Composable
expect fun ConfigureEdgeToEdge()

/**
 * Common platform theme utilities
 */
object PlatformThemeUtils {
    /**
     * Get appropriate status bar color based on platform and theme
     */
    @Composable
    fun getStatusBarColor(colorScheme: ColorScheme, config: PlatformThemeConfig): Color {
        return when (config.preferredNavigationStyle) {
            NavigationStyle.SYSTEM_BARS -> colorScheme.surface
            NavigationStyle.HOME_INDICATOR -> Color.Transparent
            NavigationStyle.HYBRID -> colorScheme.surface
        }
    }

    /**
     * Get navigation bar color
     */
    @Composable
    fun getNavigationBarColor(colorScheme: ColorScheme, config: PlatformThemeConfig): Color {
        return when (config.preferredNavigationStyle) {
            NavigationStyle.SYSTEM_BARS -> colorScheme.surfaceContainer
            NavigationStyle.HOME_INDICATOR -> Color.Transparent
            NavigationStyle.HYBRID -> colorScheme.surfaceContainer
        }
    }

    /**
     * Check if content should adjust for notch/dynamic island
     */
    fun shouldAdjustForNotch(config: PlatformThemeConfig): Boolean {
        return config.hasNotch
    }

    // Platform-specific functions are defined outside the object
}

/**
 * Safe area insets for platform-specific layouts
 */
data class SafeAreaInsets(
    val top: androidx.compose.ui.unit.Dp = 0.dp,
    val bottom: androidx.compose.ui.unit.Dp = 0.dp,
    val left: androidx.compose.ui.unit.Dp = 0.dp,
    val right: androidx.compose.ui.unit.Dp = 0.dp
)

/**
 * Platform-adaptive theme that automatically adjusts based on platform capabilities
 */
@Composable
fun PlatformAdaptiveTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val platformConfig = getPlatformThemeConfig()
    val shouldUseDynamicColor = dynamicColor && platformConfig.supportsDynamicColors

    PlatformThemeAdjustments {
        PocTheme(
            darkTheme = darkTheme,
            dynamicColor = shouldUseDynamicColor
        ) {
            content()
        }
    }
}