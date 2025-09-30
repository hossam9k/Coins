package org.poc.app.shared.design.theme.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.poc.app.shared.design.foundation.colors.*
import org.poc.app.shared.design.foundation.typography.pocTypography
import org.poc.app.shared.design.foundation.shapes.PocShapes
import org.poc.app.shared.design.theme.DarkSemanticColors
import org.poc.app.shared.design.theme.LightSemanticColors
import org.poc.app.shared.design.theme.LocalSemanticColors

internal val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    scrim = ScrimLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    inversePrimary = InversePrimaryLight,
    surfaceDim = SurfaceDimLight,
    surfaceBright = SurfaceBrightLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
)

internal val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    scrim = ScrimDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inversePrimary = InversePrimaryDark,
    surfaceDim = SurfaceDimDark,
    surfaceBright = SurfaceBrightDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
)

/**
 * Enhanced POC Theme with dynamic color support
 *
 * @param darkTheme Whether to use dark theme. Defaults to system preference
 * @param dynamicColor Whether to use dynamic colors (Android 12+). Defaults to true where supported
 * @param highContrast Whether to use high contrast colors for accessibility
 * @param content The content to be themed
 */
@Composable
internal fun PocTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        highContrast = highContrast
    )

    val semanticColors = if (darkTheme) DarkSemanticColors else LightSemanticColors

    CompositionLocalProvider(
        LocalSemanticColors provides semanticColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = pocTypography(),
            shapes = PocShapes,
            content = content,
        )
    }
}

/**
 * Get appropriate color scheme based on preferences and platform capabilities
 */
@Composable
private fun getColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    highContrast: Boolean
): ColorScheme {
    return when {
        // High contrast takes precedence
        highContrast -> getHighContrastColorScheme(darkTheme)

        // Dynamic colors (platform-specific implementation)
        dynamicColor && isDynamicColorSupported() -> {
            getPlatformColorScheme(darkTheme, dynamicColor)
        }

        // Fallback to custom brand colors
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }
}

/**
 * Check if dynamic colors are supported on current platform
 */
@Composable
expect fun isDynamicColorSupported(): Boolean

/**
 * Get high contrast color scheme for accessibility
 */
private fun getHighContrastColorScheme(darkTheme: Boolean): ColorScheme {
    return if (darkTheme) {
        // High contrast dark scheme
        darkColorScheme(
            primary = androidx.compose.ui.graphics.Color.White,
            onPrimary = androidx.compose.ui.graphics.Color.Black,
            background = androidx.compose.ui.graphics.Color.Black,
            onBackground = androidx.compose.ui.graphics.Color.White,
            surface = androidx.compose.ui.graphics.Color.Black,
            onSurface = androidx.compose.ui.graphics.Color.White,
        )
    } else {
        // High contrast light scheme
        lightColorScheme(
            primary = androidx.compose.ui.graphics.Color.Black,
            onPrimary = androidx.compose.ui.graphics.Color.White,
            background = androidx.compose.ui.graphics.Color.White,
            onBackground = androidx.compose.ui.graphics.Color.Black,
            surface = androidx.compose.ui.graphics.Color.White,
            onSurface = androidx.compose.ui.graphics.Color.Black,
        )
    }
}