package org.poc.app.shared.design.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit

/**
 * Accessibility enhancements for the theme system
 * Provides WCAG compliant sizing, spacing, and interaction targets
 */

/**
 * Accessibility configuration data class
 */
data class AccessibilityConfig(
    val fontScale: Float = 1.0f,
    val touchTargetScale: Float = 1.0f,
    val reducedMotion: Boolean = false,
    val highContrast: Boolean = false,
    val screenReaderEnabled: Boolean = false
)

/**
 * WCAG compliant dimensions
 */
object AccessibilityDimensions {
    // Minimum touch target size (WCAG AA: 44dp, AAA: 48dp)
    val minTouchTarget = 48.dp
    val recommendedTouchTarget = 56.dp

    // Text spacing for readability
    val minTextSpacing = 16.dp
    val paragraphSpacing = 24.dp

    // Focus indicators
    val focusBorderWidth = 2.dp
    val focusCornerRadius = 4.dp

    // Safe areas for content
    val screenEdgeMargin = 16.dp
    val contentMaxWidth = 600.dp // For better readability

    // Animation durations for reduced motion (in milliseconds)
    const val reducedMotionDuration = 0
    const val normalMotionDuration = 300
}

/**
 * Accessibility-aware spacing system
 */
object AccessibilitySpacing {
    @Composable
    fun getTouchTargetPadding(baseSize: Dp): PaddingValues {
        val density = LocalDensity.current
        val config = LocalAccessibilityConfig.current

        val minTargetSize = AccessibilityDimensions.minTouchTarget * config.touchTargetScale
        val additionalPadding = maxOf(0.dp, (minTargetSize - baseSize) / 2)

        return PaddingValues(all = additionalPadding)
    }

    @Composable
    fun getScaledSpacing(baseSpacing: Dp): Dp {
        val config = LocalAccessibilityConfig.current
        return baseSpacing * config.touchTargetScale
    }
}

/**
 * Font scaling utilities
 */
object AccessibilityFonts {
    @Composable
    fun getScaledFontSize(baseSize: TextUnit): TextUnit {
        val config = LocalAccessibilityConfig.current
        return baseSize * config.fontScale
    }

    @Composable
    fun getAdjustedLineHeight(baseLineHeight: TextUnit, fontSize: TextUnit): TextUnit {
        // Ensure proper line height ratio for readability
        val minLineHeightRatio = 1.5f
        val calculatedLineHeight = fontSize * minLineHeightRatio
        return maxOf(baseLineHeight.value, calculatedLineHeight.value).sp
    }
}

/**
 * Accessibility color utilities
 */
object AccessibilityColors {
    /**
     * Get focus indicator color with high contrast
     */
    @Composable
    fun getFocusColor(): androidx.compose.ui.graphics.Color {
        val config = LocalAccessibilityConfig.current
        return if (config.highContrast) {
            androidx.compose.ui.graphics.Color.Yellow
        } else {
            androidx.compose.ui.graphics.Color.Blue
        }
    }

    /**
     * Ensure sufficient color contrast ratio
     * WCAG AA: 4.5:1 for normal text, 3:1 for large text
     * WCAG AAA: 7:1 for normal text, 4.5:1 for large text
     */
    fun hasInsufficientContrast(
        foreground: androidx.compose.ui.graphics.Color,
        background: androidx.compose.ui.graphics.Color
    ): Boolean {
        val contrastRatio = calculateContrastRatio(foreground, background)
        return contrastRatio < 4.5f // WCAG AA standard
    }

    private fun calculateContrastRatio(
        color1: androidx.compose.ui.graphics.Color,
        color2: androidx.compose.ui.graphics.Color
    ): Float {
        // Simplified contrast ratio calculation
        // In production, use proper luminance calculation
        val luminance1 = (color1.red * 0.299f + color1.green * 0.587f + color1.blue * 0.114f)
        val luminance2 = (color2.red * 0.299f + color2.green * 0.587f + color2.blue * 0.114f)

        val lighter = maxOf(luminance1, luminance2)
        val darker = minOf(luminance1, luminance2)

        return (lighter + 0.05f) / (darker + 0.05f)
    }
}

/**
 * Motion and animation accessibility
 */
object AccessibilityMotion {
    @Composable
    fun getAnimationDuration(normalDuration: Int): Int {
        val config = LocalAccessibilityConfig.current
        return if (config.reducedMotion) 0 else normalDuration
    }

    @Composable
    fun shouldReduceMotion(): Boolean {
        return LocalAccessibilityConfig.current.reducedMotion
    }
}

/**
 * Platform-specific accessibility detection
 */
@Composable
expect fun rememberAccessibilityConfig(): AccessibilityConfig

/**
 * Composition local for accessibility configuration
 */
val LocalAccessibilityConfig = compositionLocalOf { AccessibilityConfig() }

/**
 * Accessibility provider composable
 */
@Composable
fun AccessibilityProvider(
    config: AccessibilityConfig = rememberAccessibilityConfig(),
    content: @Composable () -> Unit
) {
    // Apply font scaling to density
    val scaledDensity = LocalDensity.current.let { density ->
        Density(
            density = density.density,
            fontScale = density.fontScale * config.fontScale
        )
    }

    CompositionLocalProvider(
        LocalAccessibilityConfig provides config,
        LocalDensity provides scaledDensity
    ) {
        content()
    }
}