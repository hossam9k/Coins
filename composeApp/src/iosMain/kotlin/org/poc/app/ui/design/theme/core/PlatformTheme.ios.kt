package org.poc.app.ui.theme.core

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * iOS platform-specific theme implementation
 * Simplified to essentials only - no over-engineering
 */

@Composable
actual fun getPlatformThemeConfig(): PlatformThemeConfig {
    return PlatformThemeConfig(
        supportsDynamicColors = false, // iOS doesn't support Material You
        supportsHighRefreshRate = false, // Not needed
        hasNotch = false, // Not needed
        preferredNavigationStyle = NavigationStyle.HOME_INDICATOR,
        systemAccentColor = null // Not needed
    )
}

@Composable
actual fun getPlatformColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean
): ColorScheme {
    // iOS doesn't support Material You dynamic colors
    // Always use brand colors
    return if (darkTheme) DarkColorScheme else LightColorScheme
}

@Composable
actual fun PlatformThemeAdjustments(content: @Composable () -> Unit) {
    // iOS handles safe areas automatically with Compose
    // No special adjustments needed
    content()
}

@Composable
actual fun getSafeAreaInsets(): SafeAreaInsets {
    // Compose for iOS handles this automatically
    return SafeAreaInsets()
}

@Composable
actual fun ConfigureEdgeToEdge() {
    // iOS handles this automatically
    // No manual configuration needed
}