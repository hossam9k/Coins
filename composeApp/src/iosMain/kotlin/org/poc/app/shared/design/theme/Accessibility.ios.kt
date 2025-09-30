package org.poc.app.shared.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIAccessibilityIsReduceMotionEnabled
import platform.UIKit.UIAccessibilityDarkerSystemColorsEnabled
import platform.UIKit.UIAccessibilityIsVoiceOverRunning
import platform.UIKit.UIAccessibilityIsBoldTextEnabled
import platform.UIKit.UIAccessibilityIsReduceTransparencyEnabled

/**
 * iOS-specific accessibility configuration
 * Integrates with iOS accessibility APIs
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberAccessibilityConfig(): AccessibilityConfig {
    return remember {
        AccessibilityConfig(
            fontScale = getSystemFontScale(),
            touchTargetScale = getTouchTargetScale(),
            reducedMotion = UIAccessibilityIsReduceMotionEnabled(),
            highContrast = UIAccessibilityDarkerSystemColorsEnabled(),
            screenReaderEnabled = UIAccessibilityIsVoiceOverRunning()
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun getSystemFontScale(): Float {
    // iOS uses Dynamic Type for font scaling
    // This would need UIFont.preferredFont integration for full implementation
    // For now, return sensible defaults based on accessibility settings
    return if (UIAccessibilityIsBoldTextEnabled()) 1.1f else 1.0f
}

@OptIn(ExperimentalForeignApi::class)
private fun getTouchTargetScale(): Float {
    // iOS doesn't have direct touch target scaling
    // Use accessibility settings as proxy for user preferences
    return when {
        UIAccessibilityIsBoldTextEnabled() -> 1.2f
        UIAccessibilityIsReduceTransparencyEnabled() -> 1.1f
        else -> 1.0f
    }
}