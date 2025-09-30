package org.poc.app.shared.design.theme.core

import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets as ComposeWindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Android-specific theme implementation
 */
@Composable
actual fun getPlatformThemeConfig(): PlatformThemeConfig {
    val context = LocalContext.current
    val displayMetrics = remember { context.resources.displayMetrics }

    return remember {
        PlatformThemeConfig(
            supportsDynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
            supportsHighRefreshRate = supportsHighRefreshRate(context),
            hasNotch = hasDisplayCutout(context),
            preferredNavigationStyle = getPreferredNavigationStyle(context),
            systemAccentColor = getSystemAccentColor(context)
        )
    }
}

@Composable
actual fun getPlatformColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean
): ColorScheme {
    val context = LocalContext.current

    return remember(darkTheme, dynamicColor) {
        when {
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
}

@Composable
actual fun PlatformThemeAdjustments(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current

    // Configure edge-to-edge display with proper status bar handling
    LaunchedEffect(Unit) {
        if (context is ComponentActivity) {
            context.enableEdgeToEdge()
        }
    }

    // Configure system UI appearance
    SideEffect {
        if (context is ComponentActivity) {
            val window = context.window
            val windowInsetsController = WindowCompat.getInsetsController(window, view)

            // Configure system bars appearance based on system theme
            val isDarkTheme = isSystemInDarkTheme(context)
            windowInsetsController.isAppearanceLightStatusBars = !isDarkTheme
            windowInsetsController.isAppearanceLightNavigationBars = !isDarkTheme
        }
    }

    content()
}

private fun isSystemInDarkTheme(context: Context): Boolean {
    return (context.resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
}

@Composable
actual fun getSafeAreaInsets(): SafeAreaInsets {
    val density = LocalDensity.current
    val systemBars = ComposeWindowInsets.systemBars
    val context = LocalContext.current

    return remember {
        with(density) {
            SafeAreaInsets(
                top = systemBars.getTop(density).toDp(),
                bottom = systemBars.getBottom(density).toDp(),
                left = systemBars.getLeft(density, layoutDirection = androidx.compose.ui.unit.LayoutDirection.Ltr).toDp(),
                right = systemBars.getRight(density, layoutDirection = androidx.compose.ui.unit.LayoutDirection.Ltr).toDp()
            )
        }
    }
}

@Composable
actual fun ConfigureEdgeToEdge() {
    val context = LocalContext.current
    val view = LocalView.current

    LaunchedEffect(Unit) {
        if (context is ComponentActivity) {
            context.enableEdgeToEdge()
        }
    }

    // Additional system UI configuration
    SideEffect {
        if (context is ComponentActivity) {
            val window = context.window
            val windowInsetsController = WindowCompat.getInsetsController(window, view)

            // Configure system bars to have consistent behavior
            // The behavior is handled automatically by WindowCompat
        }
    }
}

/**
 * Android-specific helper functions for enhanced theme integration
 */

/**
 * Check if device has display cutout (notch/punch hole)
 */
private fun hasDisplayCutout(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        try {
            val activity = context as? ComponentActivity
            activity?.window?.decorView?.rootWindowInsets?.displayCutout != null
        } catch (e: Exception) {
            false
        }
    } else {
        false
    }
}

/**
 * Check if device supports high refresh rate displays
 */
private fun supportsHighRefreshRate(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        try {
            val activity = context as? ComponentActivity
            val display = activity?.display
            val supportedModes = display?.supportedModes ?: emptyArray()
            supportedModes.any { it.refreshRate > 60f }
        } catch (e: Exception) {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        }
    } else {
        false
    }
}

/**
 * Get preferred navigation style based on device capabilities
 */
private fun getPreferredNavigationStyle(context: Context): NavigationStyle {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        try {
            val activity = context as? ComponentActivity
            val hasGestureNavigation = activity?.let { act ->
                val resources = act.resources
                val resourceId = resources.getIdentifier(
                    "config_navBarInteractionMode",
                    "integer",
                    "android"
                )
                if (resourceId > 0) {
                    resources.getInteger(resourceId) == 2 // Gesture navigation
                } else {
                    false
                }
            } ?: false

            if (hasGestureNavigation) NavigationStyle.HYBRID else NavigationStyle.SYSTEM_BARS
        } catch (e: Exception) {
            NavigationStyle.SYSTEM_BARS
        }
    } else {
        NavigationStyle.SYSTEM_BARS
    }
}

/**
 * Extract system accent color with proper Material You integration
 */
private fun getSystemAccentColor(context: Context): Color? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        try {
            // Extract accent color from system theme attributes
            val typedArray = context.obtainStyledAttributes(intArrayOf(
                android.R.attr.colorAccent,
                android.R.attr.colorPrimary
            ))

            val accentColor = typedArray.getColor(0, 0)
            val primaryColor = typedArray.getColor(1, 0)
            typedArray.recycle()

            // Prefer accent color, fallback to primary
            val colorInt = if (accentColor != 0) accentColor else primaryColor
            if (colorInt != 0) Color(colorInt) else null
        } catch (e: Exception) {
            // Fallback: try to extract from Material You dynamic colors
            try {
                val lightColorScheme = dynamicLightColorScheme(context)
                lightColorScheme.primary
            } catch (e2: Exception) {
                null
            }
        }
    } else {
        null
    }
}

/**
 * Performance-optimized device capability detection
 */
object AndroidThemeCapabilities {
    private var cachedCapabilities: Map<String, Any>? = null

    fun getDeviceCapabilities(context: Context): Map<String, Any> {
        return cachedCapabilities ?: run {
            val capabilities = mutableMapOf<String, Any>()

            // Screen metrics
            val displayMetrics = context.resources.displayMetrics
            capabilities["screenDensity"] = displayMetrics.density
            capabilities["screenWidthPx"] = displayMetrics.widthPixels
            capabilities["screenHeightPx"] = displayMetrics.heightPixels

            // Hardware capabilities
            capabilities["hasNotch"] = hasDisplayCutout(context)
            capabilities["supportsHighRefreshRate"] = supportsHighRefreshRate(context)
            capabilities["supportsDynamicColors"] = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

            // System preferences
            capabilities["preferredNavigationStyle"] = getPreferredNavigationStyle(context).name
            capabilities["systemAccentColor"] = getSystemAccentColor(context)?.toArgb() ?: 0

            // API level info
            capabilities["androidApiLevel"] = Build.VERSION.SDK_INT
            capabilities["androidVersion"] = Build.VERSION.RELEASE

            cachedCapabilities = capabilities.toMap()
            capabilities.toMap()
        }
    }

    fun clearCache() {
        cachedCapabilities = null
    }
}

/**
 * Android-specific theme validation and optimization
 */
object AndroidThemeValidator {

    /**
     * Validate Material You dynamic colors compatibility
     */
    fun validateDynamicColors(context: Context): ThemeValidationResult {
        val issues = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        // Check API level support
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            warnings.add("Dynamic colors require Android 12+ (API 31). Fallback colors will be used.")
        }

        // Validate dynamic color schemes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val lightScheme = dynamicLightColorScheme(context)
                val darkScheme = dynamicDarkColorScheme(context)

                // Basic validation of dynamic colors
                // TODO: Integrate with proper ThemeValidator when available
            } catch (e: Exception) {
                issues.add("Failed to validate dynamic color schemes: ${e.message}")
            }
        }

        return ThemeValidationResult(
            isValid = issues.isEmpty(),
            issues = issues,
            warnings = warnings
        )
    }

    /**
     * Validate edge-to-edge display compatibility
     */
    fun validateEdgeToEdge(context: Context): ThemeValidationResult {
        val issues = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        try {
            val activity = context as? ComponentActivity
            if (activity == null) {
                warnings.add("Edge-to-edge validation requires ComponentActivity context")
                return ThemeValidationResult(true, emptyList(), warnings)
            }

            // Check display cutout handling
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val cutout = activity.window?.decorView?.rootWindowInsets?.displayCutout
                if (cutout != null) {
                    val safeInsetTop = cutout.safeInsetTop
                    val safeInsetBottom = cutout.safeInsetBottom

                    if (safeInsetTop > 0 || safeInsetBottom > 0) {
                        warnings.add("Device has display cutout. Ensure proper safe area handling.")
                    }
                }
            }

            // Validate status bar configuration
            val window = activity.window
            if (window.statusBarColor != android.graphics.Color.TRANSPARENT) {
                warnings.add("Status bar should be transparent for proper edge-to-edge experience")
            }

            if (window.navigationBarColor != android.graphics.Color.TRANSPARENT) {
                warnings.add("Navigation bar should be transparent for proper edge-to-edge experience")
            }

        } catch (e: Exception) {
            issues.add("Edge-to-edge validation failed: ${e.message}")
        }

        return ThemeValidationResult(
            isValid = issues.isEmpty(),
            issues = issues,
            warnings = warnings
        )
    }

    /**
     * Validate system UI controller configuration
     */
    fun validateSystemUI(context: Context): ThemeValidationResult {
        val issues = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        try {
            val activity = context as? ComponentActivity
            if (activity == null) {
                warnings.add("System UI validation requires ComponentActivity context")
                return ThemeValidationResult(true, emptyList(), warnings)
            }

            // Check system bars behavior
            val window = activity.window
            val decorView = window.decorView
            val systemUiVisibility = decorView.systemUiVisibility

            // Validate immersive mode settings
            val hasImmersiveFlags = (systemUiVisibility and
                android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) != 0

            if (hasImmersiveFlags) {
                warnings.add("Immersive mode detected. Ensure proper handling of system bar visibility changes.")
            }

            // Check for deprecated flags
            @Suppress("DEPRECATION")
            val hasDeprecatedFlags = (systemUiVisibility and
                android.view.View.SYSTEM_UI_FLAG_FULLSCREEN) != 0

            if (hasDeprecatedFlags) {
                issues.add("Deprecated system UI flags detected. Use WindowInsetsController for API 30+")
            }

        } catch (e: Exception) {
            issues.add("System UI validation failed: ${e.message}")
        }

        return ThemeValidationResult(
            isValid = issues.isEmpty(),
            issues = issues,
            warnings = warnings
        )
    }

    /**
     * Comprehensive Android theme validation
     */
    fun validateAndroidTheme(context: Context): ThemeValidationResult {
        val dynamicResult = validateDynamicColors(context)
        val edgeToEdgeResult = validateEdgeToEdge(context)
        val systemUIResult = validateSystemUI(context)

        val allIssues = mutableListOf<String>()
        val allWarnings = mutableListOf<String>()

        allIssues.addAll(dynamicResult.issues)
        allIssues.addAll(edgeToEdgeResult.issues)
        allIssues.addAll(systemUIResult.issues)

        allWarnings.addAll(dynamicResult.warnings)
        allWarnings.addAll(edgeToEdgeResult.warnings)
        allWarnings.addAll(systemUIResult.warnings)

        return ThemeValidationResult(
            isValid = allIssues.isEmpty(),
            issues = allIssues,
            warnings = allWarnings
        )
    }
}

/**
 * Theme validation result data class
 */
data class ThemeValidationResult(
    val isValid: Boolean,
    val issues: List<String>,
    val warnings: List<String>
)