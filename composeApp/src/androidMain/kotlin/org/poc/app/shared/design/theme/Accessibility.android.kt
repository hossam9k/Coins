package org.poc.app.shared.design.theme

import android.content.Context
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Android-specific accessibility configuration
 * Reads system accessibility settings
 */
@Composable
actual fun rememberAccessibilityConfig(): AccessibilityConfig {
    val context = LocalContext.current

    return AccessibilityConfig(
        fontScale = getFontScale(context),
        touchTargetScale = getTouchTargetScale(context),
        reducedMotion = isReducedMotionEnabled(context),
        highContrast = isHighContrastEnabled(context),
        screenReaderEnabled = isScreenReaderEnabled(context)
    )
}

private fun getFontScale(context: Context): Float {
    return context.resources.configuration.fontScale
}

private fun getTouchTargetScale(context: Context): Float {
    // Android doesn't have a direct touch target scaling setting
    // Use font scale as proxy for UI scaling preferences
    val fontScale = getFontScale(context)
    return when {
        fontScale >= 1.3f -> 1.2f
        fontScale >= 1.15f -> 1.1f
        else -> 1.0f
    }
}

private fun isReducedMotionEnabled(context: Context): Boolean {
    return try {
        val scale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1.0f
        )
        scale == 0.0f
    } catch (_: Exception) {
        false
    }
}

private fun isHighContrastEnabled(context: Context): Boolean {
    return try {
        // Check for high contrast text setting (API 24+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Settings.Secure.getInt(
                context.contentResolver,
                "high_text_contrast_enabled", // This is the actual setting name
                0
            ) == 1
        } else {
            // Fallback for older versions - check if any accessibility service is running
            val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            accessibilityManager.isEnabled && accessibilityManager.isTouchExplorationEnabled
        }
    } catch (_: Exception) {
        false
    }
}

private fun isScreenReaderEnabled(context: Context): Boolean {
    return try {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        accessibilityManager.isEnabled && accessibilityManager.isTouchExplorationEnabled
    } catch (_: Exception) {
        false
    }
}