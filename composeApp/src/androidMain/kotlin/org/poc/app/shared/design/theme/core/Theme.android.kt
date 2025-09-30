package org.poc.app.shared.design.theme.core

import android.os.Build
import androidx.compose.runtime.Composable

/**
 * Android-specific implementation for dynamic color support
 * Dynamic colors are supported on Android 12+ (API 31+)
 */
@Composable
actual fun isDynamicColorSupported(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}