package org.poc.app.ui.theme.core

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Android platform-specific theme implementation
 * Simplified to essentials only - no over-engineering
 */

@Composable
actual fun getPlatformThemeConfig(): PlatformThemeConfig {
    return PlatformThemeConfig(
        supportsDynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
        supportsHighRefreshRate = false, // Not needed
        hasNotch = false, // Not needed
        preferredNavigationStyle = NavigationStyle.SYSTEM_BARS,
        systemAccentColor = null // Not needed
    )
}

@Composable
actual fun getPlatformColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean
): ColorScheme {
    val context = LocalContext.current

    // Use dynamic colors on Android 12+ if available
    return when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }
}

@Composable
actual fun PlatformThemeAdjustments(content: @Composable () -> Unit) {
    // Android handles edge-to-edge automatically with Material 3
    // No special adjustments needed
    content()
}

@Composable
actual fun getSafeAreaInsets(): SafeAreaInsets {
    // Material 3 handles this automatically
    return SafeAreaInsets()
}

@Composable
actual fun ConfigureEdgeToEdge() {
    // Material 3 handles this automatically
    // No manual configuration needed
}