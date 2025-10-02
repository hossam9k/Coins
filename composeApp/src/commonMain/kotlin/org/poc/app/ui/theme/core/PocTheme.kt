package org.poc.app.ui.theme.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import org.poc.app.ui.foundation.colors.BackgroundDark
import org.poc.app.ui.foundation.colors.BackgroundLight
import org.poc.app.ui.foundation.colors.ErrorContainerDark
import org.poc.app.ui.foundation.colors.ErrorContainerLight
import org.poc.app.ui.foundation.colors.ErrorDark
import org.poc.app.ui.foundation.colors.ErrorLight
import org.poc.app.ui.foundation.colors.InverseOnSurfaceDark
import org.poc.app.ui.foundation.colors.InverseOnSurfaceLight
import org.poc.app.ui.foundation.colors.InversePrimaryDark
import org.poc.app.ui.foundation.colors.InversePrimaryLight
import org.poc.app.ui.foundation.colors.InverseSurfaceDark
import org.poc.app.ui.foundation.colors.InverseSurfaceLight
import org.poc.app.ui.foundation.colors.OnBackgroundDark
import org.poc.app.ui.foundation.colors.OnBackgroundLight
import org.poc.app.ui.foundation.colors.OnErrorContainerDark
import org.poc.app.ui.foundation.colors.OnErrorContainerLight
import org.poc.app.ui.foundation.colors.OnErrorDark
import org.poc.app.ui.foundation.colors.OnErrorLight
import org.poc.app.ui.foundation.colors.OnPrimaryContainerDark
import org.poc.app.ui.foundation.colors.OnPrimaryContainerLight
import org.poc.app.ui.foundation.colors.OnPrimaryDark
import org.poc.app.ui.foundation.colors.OnPrimaryLight
import org.poc.app.ui.foundation.colors.OnSecondaryContainerDark
import org.poc.app.ui.foundation.colors.OnSecondaryContainerLight
import org.poc.app.ui.foundation.colors.OnSecondaryDark
import org.poc.app.ui.foundation.colors.OnSecondaryLight
import org.poc.app.ui.foundation.colors.OnSurfaceDark
import org.poc.app.ui.foundation.colors.OnSurfaceLight
import org.poc.app.ui.foundation.colors.OnSurfaceVariantDark
import org.poc.app.ui.foundation.colors.OnSurfaceVariantLight
import org.poc.app.ui.foundation.colors.OnTertiaryContainerDark
import org.poc.app.ui.foundation.colors.OnTertiaryContainerLight
import org.poc.app.ui.foundation.colors.OnTertiaryDark
import org.poc.app.ui.foundation.colors.OnTertiaryLight
import org.poc.app.ui.foundation.colors.OutlineDark
import org.poc.app.ui.foundation.colors.OutlineLight
import org.poc.app.ui.foundation.colors.OutlineVariantDark
import org.poc.app.ui.foundation.colors.OutlineVariantLight
import org.poc.app.ui.foundation.colors.PrimaryContainerDark
import org.poc.app.ui.foundation.colors.PrimaryContainerLight
import org.poc.app.ui.foundation.colors.PrimaryDark
import org.poc.app.ui.foundation.colors.PrimaryLight
import org.poc.app.ui.foundation.colors.ScrimDark
import org.poc.app.ui.foundation.colors.ScrimLight
import org.poc.app.ui.foundation.colors.SecondaryContainerDark
import org.poc.app.ui.foundation.colors.SecondaryContainerLight
import org.poc.app.ui.foundation.colors.SecondaryDark
import org.poc.app.ui.foundation.colors.SecondaryLight
import org.poc.app.ui.foundation.colors.SurfaceBrightDark
import org.poc.app.ui.foundation.colors.SurfaceBrightLight
import org.poc.app.ui.foundation.colors.SurfaceContainerDark
import org.poc.app.ui.foundation.colors.SurfaceContainerHighDark
import org.poc.app.ui.foundation.colors.SurfaceContainerHighLight
import org.poc.app.ui.foundation.colors.SurfaceContainerHighestDark
import org.poc.app.ui.foundation.colors.SurfaceContainerHighestLight
import org.poc.app.ui.foundation.colors.SurfaceContainerLight
import org.poc.app.ui.foundation.colors.SurfaceContainerLowDark
import org.poc.app.ui.foundation.colors.SurfaceContainerLowLight
import org.poc.app.ui.foundation.colors.SurfaceContainerLowestDark
import org.poc.app.ui.foundation.colors.SurfaceContainerLowestLight
import org.poc.app.ui.foundation.colors.SurfaceDark
import org.poc.app.ui.foundation.colors.SurfaceDimDark
import org.poc.app.ui.foundation.colors.SurfaceDimLight
import org.poc.app.ui.foundation.colors.SurfaceLight
import org.poc.app.ui.foundation.colors.SurfaceVariantDark
import org.poc.app.ui.foundation.colors.SurfaceVariantLight
import org.poc.app.ui.foundation.colors.TertiaryContainerDark
import org.poc.app.ui.foundation.colors.TertiaryContainerLight
import org.poc.app.ui.foundation.colors.TertiaryDark
import org.poc.app.ui.foundation.colors.TertiaryLight
import org.poc.app.ui.foundation.shapes.PocShapes
import org.poc.app.ui.foundation.typography.pocTypography

/**
 * Main theme composable - simplified architecture
 */
@Composable
fun PocTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val config = getPlatformThemeConfig()
    val colorScheme =
        when {
            // Try dynamic colors first (Android 12+)
            config.supportsDynamicColors -> getPlatformColorScheme(darkTheme, true)
            // Fallback to brand colors
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    PlatformThemeAdjustments {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = pocTypography(),
            shapes = PocShapes,
            content = content,
        )
    }
}

// Brand color schemes
internal val LightColorScheme =
    lightColorScheme(
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

internal val DarkColorScheme =
    darkColorScheme(
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
